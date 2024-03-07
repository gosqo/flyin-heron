package com.vong.manidues.auth;

import com.vong.manidues.config.JwtService;
import com.vong.manidues.member.Member;
import com.vong.manidues.member.MemberRepository;
import com.vong.manidues.token.JwtExceptionResponse;
import com.vong.manidues.token.Token;
import com.vong.manidues.token.TokenRepository;
import com.vong.manidues.token.TokenType;
import com.vong.manidues.utility.CustomServletResponse;
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
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomServletResponse customServletResponse;

    private void revokeAllMemberTokens(Member member) {
        List<Token> validMemberTokens = tokenRepository.findAllValidTokensByMember(member.getId());
        if (validMemberTokens.isEmpty()) return;
        validMemberTokens.forEach(t -> {
            t.setExpired(true);
            t.setRevoked(true);
        });
        tokenRepository.saveAll(validMemberTokens);
    }

    private void saveMemberToken(Member savedMember, String jwtToken) {
        Token token = Token.builder()
                .member(savedMember)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .build();
        tokenRepository.save(token);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow();
        String accessToken = jwtService.generateAccessToken(member);
        String refreshToken = jwtService.generateRefreshToken(member);

        revokeAllMemberTokens(member);
        saveMemberToken(member, refreshToken);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

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

        try {
            userEmail = jwtService.extractUsername(refreshToken);// extract the userEmail from refreshToken
            if (userEmail != null) {
                Member member = this.memberRepository.findByEmail(userEmail).orElseThrow();

                String accessToken = jwtService.generateAccessToken(member);

                refreshTokenExpiration = jwtService
                        .extractClaim(refreshToken, Claims::getExpiration)
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                gapToExpiration = ChronoUnit.DAYS.between(today, refreshTokenExpiration);

                revokeAllMemberTokens(member);

                if (gapToExpiration <= 7) {
                    String renewedRefreshToken = jwtService.generateRefreshToken(member);


                    // 만료기간이 7 일 이하로 남아 새로 갱신한 리프레시 토큰을 디비에 저장.
                    saveMemberToken(member, renewedRefreshToken);

                    // 기존의 리프레시 토큰은 삭제하도록
//                    tokenRepository.delete(tokenRepository.findByToken(refreshToken).orElseThrow());
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

            customServletResponse.jsonResponse(response, 401,
                    JwtExceptionResponse.builder()
                            .exceptionMessage("인증 정보가 필요합니다.")
                            .build()
            );
        }

        return null;
    }
}
