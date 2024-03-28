package com.vong.manidues.auth;

import com.vong.manidues.config.JwtService;
import com.vong.manidues.member.Member;
import com.vong.manidues.member.MemberRepository;
import com.vong.manidues.token.Token;
import com.vong.manidues.token.TokenRepository;
import com.vong.manidues.utility.HttpResponseWithBody;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final HttpResponseWithBody responseWithBody;

    @SuppressWarnings("null")
    private void saveMemberToken(Member savedMember, String jwtToken) {
        Token token = Token.builder()
                .member(savedMember)
                .token(jwtToken)
                .build();
        tokenRepository.save(token);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // TODO Authentication, AuthenticationManager,
        //  UsernamePasswordAuthenticationToken 객체에 대한 학습(구조, 흐름)

        String accessToken = null;
        String refreshToken = null;
        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException ex) {
            log.info("\n{}", ex.getMessage());
        }

        Member member = memberRepository.findByEmail(request.getEmail())
                .orElse(null);

        if (member != null) {

            accessToken = jwtService.generateAccessToken(member);
            refreshToken = jwtService.generateRefreshToken(member);

            saveMemberToken(member, refreshToken);

        }

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     *     액세스 토큰 갱신(refresh) 및 발급.
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
     * */
    public AuthenticationResponse refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        final LocalDate today = LocalDate.now();
        final LocalDate refreshTokenExpiration;
        final long gapToExpiration;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        refreshToken = authHeader.substring(7);

        // refreshToken 의 유효성에 대한 try / catch
        try {
            userEmail = jwtService.extractUserEmail(refreshToken);// extract the userEmail from refreshToken
            if (userEmail != null) {
                Member member = this.memberRepository.findByEmail(userEmail).orElseThrow();

                String accessToken = jwtService.generateAccessToken(member);

                refreshTokenExpiration = jwtService
                        .extractClaim(refreshToken, Claims::getExpiration)
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                gapToExpiration = ChronoUnit.DAYS.between(today, refreshTokenExpiration);

                if (gapToExpiration <= 7) {
                    String renewedRefreshToken = jwtService.generateRefreshToken(member);

                    // 만료기간이 7 일 이하로 남아 새로 갱신한 리프레시 토큰을 디비에 저장.
                    saveMemberToken(member, renewedRefreshToken);

                    // 기존의 리프레시 토큰은 삭제.
                    log.info("deleted token count: {}", tokenRepository.deleteByToken(refreshToken));

                    return AuthenticationResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(renewedRefreshToken)
                            .build();
                }

                // 기존의 리프레시 토큰을 반환하는 경우 디비에 해당 토큰이 이미 존재.
                // 중복 자료가 발생하지 않도록
                return AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
            }
        } catch (ExpiredJwtException | SignatureException | MalformedJwtException e) {
            log.info("caught error: {}", e.getMessage());
            responseWithBody.jsonResponse(response, 401, "인증정보가 필요합니다.");
        }

        return null;
    }
}
