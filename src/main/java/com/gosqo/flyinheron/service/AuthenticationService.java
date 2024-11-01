package com.gosqo.flyinheron.service;

import com.gosqo.flyinheron.domain.Member;
import com.gosqo.flyinheron.domain.Token;
import com.gosqo.flyinheron.dto.auth.AuthenticationRequest;
import com.gosqo.flyinheron.global.exception.custom.DebugNeededException;
import com.gosqo.flyinheron.repository.MemberRepository;
import com.gosqo.flyinheron.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
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

    public Map<String, String> authenticate(AuthenticationRequest request) throws AuthenticationException {
        final Map<String, String> tokensToResponse = new HashMap<>();

        authenticationManager.authenticate( // throws AuthenticationException
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // authenticationManager.authenticate(Authentication) 을 통과하면 아래 예외는 던져지지 않음.
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new DebugNeededException("인증 통과 후, 조회되지 않는 회원"));

        String refreshToken = jwtService.generateRefreshToken(member);

        saveMemberToken(member, refreshToken);

        tokensToResponse.put("accessToken", jwtService.buildAccessTokenWithClaims(member));
        tokensToResponse.put("refreshToken", refreshToken);

        return tokensToResponse;
    }

    public Map<String, String> refreshToken(String formerRefreshToken) {
        final Map<String, String> tokensToResponse = new HashMap<>();
        final String userEmail = claimExtractor.extractUserEmail(formerRefreshToken);
        final Member member = memberRepository.findByEmail(userEmail).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 회원에 대한 조회.")
        );

        tokenRepository.findByToken(formerRefreshToken).orElseThrow(
                () -> new NoSuchElementException("데이터베이스에 존재하지 않는 리프레시 토큰.")
        );

        String refreshTokenToResponse = formerRefreshToken;

        if (needToReissue(formerRefreshToken)) {
            final String reissuedRefreshToken = jwtService.generateRefreshToken(member);

            tokenRepository.findByToken(reissuedRefreshToken).ifPresentOrElse(
                    (stored) -> {}
                    , () -> {
                        saveMemberToken(member, reissuedRefreshToken);
                        tokenRepository.deleteByToken(formerRefreshToken);
                    }
            );

            refreshTokenToResponse = reissuedRefreshToken;
        }

        tokensToResponse.put("accessToken", jwtService.buildAccessTokenWithClaims(member));
        tokensToResponse.put("refreshToken", refreshTokenToResponse);

        return tokensToResponse;
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
