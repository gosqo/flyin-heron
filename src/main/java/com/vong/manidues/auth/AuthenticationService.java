package com.vong.manidues.auth;

import com.vong.manidues.exception.custom.DebugNeededException;
import com.vong.manidues.member.Member;
import com.vong.manidues.member.MemberRepository;
import com.vong.manidues.token.JwtService;
import com.vong.manidues.token.Token;
import com.vong.manidues.token.TokenRepository;
import com.vong.manidues.utility.AuthHeaderUtility;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final AuthHeaderUtility authHeaderUtility;

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

        return buildAuthenticationResponse(accessToken, refreshToken);
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
    public AuthenticationResponse refreshToken(HttpServletRequest request)
            throws IOException {
        final String formerRefreshToken = authHeaderUtility.extractJwtFromHeader(request);
        final String userEmail = jwtService.extractUserEmail(formerRefreshToken);

        tokenRepository.findByToken(formerRefreshToken).orElseThrow(
                () -> new NoSuchElementException("데이터베이스에 존재하지 않는 리프레시 토큰.")
        );
        Member member = this.memberRepository.findByEmail(userEmail).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 회원에 대한 조회.")
        );

        if (needToReissue(formerRefreshToken)) {
            String reissuedRefreshToken = jwtService.generateRefreshToken(member);

            saveMemberToken(member, reissuedRefreshToken);
            tokenRepository.deleteByToken(formerRefreshToken);

            return buildAuthenticationResponse(member, reissuedRefreshToken);
        }
        return buildAuthenticationResponse(member, formerRefreshToken);
    }

    private AuthenticationResponse buildAuthenticationResponse(
            Member member
            , String refreshToken
    ) {
        return AuthenticationResponse.builder()
                .accessToken(jwtService.generateAccessToken(member))
                .refreshToken(refreshToken)
                .build();
    }

    private AuthenticationResponse buildAuthenticationResponse(
            String accessToken
            , String refreshToken
    ) {
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @SuppressWarnings("null")
    private void saveMemberToken(Member member, String jwtToken) {
        Token token = Token.builder()
                .member(member)
                .token(jwtToken)
                .build();
        tokenRepository.save(token);
    }

    private boolean needToReissue(String refreshToken) {
        return getGapToExpiration(refreshToken) < 8;
    }

    private int getGapToExpiration(String refreshToken) {
        final LocalDate today = LocalDate.now();
        final LocalDate refreshTokenExpiration = jwtService
                .extractClaim(refreshToken, Claims::getExpiration)
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        return (int) ChronoUnit.DAYS.between(today, refreshTokenExpiration);
    }
}
