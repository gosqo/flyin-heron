package com.vong.manidues.auth;

import com.vong.manidues.exception.custom.DebugNeededException;
import com.vong.manidues.member.Member;
import com.vong.manidues.member.MemberRepository;
import com.vong.manidues.token.JwtService;
import com.vong.manidues.token.Token;
import com.vong.manidues.token.TokenRepository;
import com.vong.manidues.utility.HttpResponseWithBody;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final HttpResponseWithBody responseWithBody;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // throws BadCredentialsException if request can not be authenticated
        // 해당 예외가 잡지 못하는 추가적인 예외가 있을지?
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        log.info("Authenticated: {}", authentication.isAuthenticated());
        log.info("Principal: {}", authentication.getPrincipal());
        log.info("Authorities: {}", authentication.getAuthorities());

        // authenticationManager.authenticate(Authentication) 을 통과하면 아래 예외는 던져지지 않음.
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new DebugNeededException("인증 통과 후, 조회되지 않는 회원"));

        String accessToken = jwtService.generateAccessToken(member);
        String refreshToken = jwtService.generateRefreshToken(member);

        saveMemberToken(member, refreshToken);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * 액세스 토큰 갱신(refresh) 및 발급.
     * <pre>
     *     클라이언트는 요청헤더에 리프레시 토큰을 담습니다.
     *     리프레시 토큰이 만료, 위조된 경우 401 상태코드로 응답합니다.
     *     리프레시 토큰이 검증에 성공하면 데이터 베이스에 조회합니다.
     *     리프레시 토큰의 만료일을 오늘과 비교합니다.
     *       만료일까지 7 일 이하로 남았다면,
     *         accessToken, refreshToken 모두 갱신하여 응답합니다.
     *       7일 이상 남았다면,
     *         accessToken 만 갱신하고, 요청헤더의 기존 refreshToken 을 함께 응답합니다.
     *
     *     해당 요청이 성공적이라면,
     *       클라이언트는 갱신된 access_token,
     *       갱신되거나 기존의 refresh_token 을 모두 브라우저에 저장합니다.
     *       응답은 json 형태입니다.
     * </pre>
     */
    public AuthenticationResponse refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        final LocalDate today = LocalDate.now();
        final long gapToExpiration;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        refreshToken = authHeader.substring(7);

        // refreshToken 의 유효성에 대한 try / catch
        try {
            tokenRepository.findByToken(refreshToken).orElseThrow(NoSuchElementException::new);
            userEmail = jwtService.extractUserEmail(refreshToken);// extract the userEmail from refreshToken

            if (userEmail != null) {

                Member member = this.memberRepository.findByEmail(userEmail).orElseThrow();
                String accessToken = jwtService.generateAccessToken(member);

                gapToExpiration = getGapToExpiration(refreshToken, today);

                if (gapToExpiration <= 7) {

                    String reissuedRefreshToken = jwtService.generateRefreshToken(member);

                    // 만료기간이 7 일 이하로 남아 새로 갱신한 리프레시 토큰을 디비에 저장.
                    saveMemberToken(member, reissuedRefreshToken);
                    // 기존의 리프레시 토큰은 삭제.
                    tokenRepository.deleteByToken(refreshToken);

                    log.info("refreshed token been successfully issued with reissuedRefreshToken.");
                    return AuthenticationResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(reissuedRefreshToken)
                            .build();
                }

                log.info("refreshed token been successfully issued.");
                return AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
            }
        } catch (NoSuchElementException ex) {
            log.warn("""
                            User tried refresh tokens with Token not exists on database.
                            Token: {}"""
                    , refreshToken
            );
            responseWithBody.setResponseWithBody(
                    response,
                    400,
                    "서버에 인증정보가 존재하지 않습니다.."
            );
        }
        return null;
    }

    @SuppressWarnings("null")
    private void saveMemberToken(Member member, String jwtToken) {
        Token token = Token.builder()
                .member(member)
                .token(jwtToken)
                .build();
        tokenRepository.save(token);
    }

    private long getGapToExpiration(String refreshToken, LocalDate today) {
        final LocalDate refreshTokenExpiration;

        refreshTokenExpiration = jwtService
                .extractClaim(refreshToken, Claims::getExpiration)
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        return ChronoUnit.DAYS.between(today, refreshTokenExpiration);
    }
}
