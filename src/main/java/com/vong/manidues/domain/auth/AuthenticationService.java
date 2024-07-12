package com.vong.manidues.domain.auth;

import com.vong.manidues.global.exception.custom.DebugNeededException;
import com.vong.manidues.domain.member.Member;
import com.vong.manidues.domain.member.MemberRepository;
import com.vong.manidues.domain.token.ClaimExtractor;
import com.vong.manidues.domain.token.JwtService;
import com.vong.manidues.domain.token.Token;
import com.vong.manidues.domain.token.TokenRepository;
import com.vong.manidues.global.utility.AuthHeaderUtility;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

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
    private final ClaimExtractor claimExtractor;
    private final AuthenticationManager authenticationManager;

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

    public AuthenticationResponse refreshToken(HttpServletRequest request) {
        final String formerRefreshToken = AuthHeaderUtility.extractJwt(request);
        final String userEmail = claimExtractor.extractUserEmail(formerRefreshToken);
        final Member member = memberRepository.findByEmail(userEmail).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 회원에 대한 조회.")
        );

        tokenRepository.findByToken(formerRefreshToken).orElseThrow(
                () -> new NoSuchElementException("데이터베이스에 존재하지 않는 리프레시 토큰.")
        );

        if (needToReissue(formerRefreshToken)) {
            final String reissuedRefreshToken = jwtService.generateRefreshToken(member);

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

    private void saveMemberToken(Member member, String jwtToken) {
        final Token token = Token.builder()
                .member(member)
                .token(jwtToken)
                .expirationDate(claimExtractor.extractExpiration(jwtToken))
                .build();
        tokenRepository.save(token);
    }

    private boolean needToReissue(String refreshToken) {
        return getGapToExpiration(refreshToken) < 8;
    }

    private int getGapToExpiration(String refreshToken) {
        final LocalDate today = LocalDate.now();
        final LocalDate refreshTokenExpiration = claimExtractor
                .extractExpiration(refreshToken)
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        return (int) ChronoUnit.DAYS.between(today, refreshTokenExpiration);
    }
}
