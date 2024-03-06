package com.vong.manidues.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vong.manidues.config.JwtService;
import com.vong.manidues.member.Member;
import com.vong.manidues.member.MemberRepository;
import com.vong.manidues.token.JwtExceptionResponse;
import com.vong.manidues.token.Token;
import com.vong.manidues.token.TokenRepository;
import com.vong.manidues.token.TokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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


    /**
     * <pre>
     * /api/v1/auth/refresh-token 으로 요청이 올 때,
     *  리프레시 토큰의 만료 기간에 따라
     *      리프레시 토큰을 함께 반환할 것인지,
     *      갱신된 액세스 토큰만 반환할 것인지 기준이 있으면 좋을 것.
     *  해당 요청의 리프레시 토큰 만료 기간이 1 주일 미만 남은 경우 (만료 전),
     *      액세스 토큰과 함께 리프레시 토큰도 함께 발급.
     *  그 외의 경우,
     *      액세스 토큰만 발급.
     *     (프론트에서는 'refresh_token' === null 의 경우,
     *     브라우저에 리프레시 토큰을 저장하지 말 것.)
     *
     *  이전에 저장되었던 리프레시 토큰은 디비에서 삭제한다?
     *
     * 리프레시 토큰이 필터를 거쳐서 넘어왔다는 것은, 만료 예외를 일으키지 않았다는 뜻.
     * 인증 서비스 내부에서 한 번 더 유효성을 검사해야할 필요성이 있을까
     *
     *
     * </pre>
     */
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

//            if (jwtService.isTokenValid(refreshToken, member)) { // 해당 절이 존재하는 이유?
                // 토큰 클레임의 이메일과, 디비 조회로 얻어 온 멤버 엔티티의 이메일이 같은지 비교,
                // 토큰이 만료되었는 지 확인

                // 해당 멤버의 엔티티를 얻기 위해 뽑은 이메일은 요청 헤더에 담겨 들어온 토큰으로 부터 온다.
                // 당연히 일치할 것,
                // 만약, 위조된 토큰이라 해당 클레임 이메일로 데이터베이스에 조회되는 개체가 없을 시,Optional<Member>에서 예외를 던짐.
                // 토큰이 만료되었는지도 확인하는데,
                // 토큰이 만료되었다면 해당 메서드 트라이 절에서 이미 예외를 던졌을 것. (필터에서도 jwt 오류를 확인, 로그 남김.)
                // 클라이언트에게는 401 인증 필요 메세지 응답.
                // 그렇다면 없어도 될 것 같은데?

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

                    saveMemberToken(member, renewedRefreshToken);

                    return AuthenticationResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(renewedRefreshToken)
                            .build();
                }

                saveMemberToken(member, refreshToken);

                return AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
//            }
            }
        } catch (ExpiredJwtException | SignatureException | MalformedJwtException e) {
            log.info("caught error: {}", e.getMessage());

            ObjectMapper mapper = new ObjectMapper();
            String jsonAsString = mapper.writeValueAsString(
                    JwtExceptionResponse.builder()
                            .statusCode(401)
                            .exceptionMessage("인증정보가 필요합니다.")
                            .build()
            );

            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON.toString());
            response.setCharacterEncoding("UTF-8");
            response.getWriter().println(jsonAsString);
        }

        return null;
    }
}
