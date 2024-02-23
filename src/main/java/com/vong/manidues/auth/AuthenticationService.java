package com.vong.manidues.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vong.manidues.config.JwtService;
import com.vong.manidues.member.Member;
import com.vong.manidues.member.MemberRepository;
import com.vong.manidues.member.Role;
import com.vong.manidues.token.Token;
import com.vong.manidues.token.TokenRepository;
import com.vong.manidues.token.TokenType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final MemberRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        Member member = Member.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .role(Role.USER)
                .build();
        Member savedMember = repository.save(member);

        String jwtToken = jwtService.generateToken(member);
        String refreshToken = jwtService.generateRefreshToken(member);

        saveMemberToken(savedMember, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

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
        Member member = repository.findByEmail(request.getEmail())
                .orElseThrow();
        String jwtToken = jwtService.generateToken(member);
        String refreshToken = jwtService.generateRefreshToken(member);

        revokeAllMemberTokens(member);
        saveMemberToken(member, jwtToken);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }


    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);// extract the userEmail from refreshToken ;

        if (userEmail != null) {
            Member member = this.repository.findByEmail(userEmail).orElseThrow();

            if (jwtService.isTokenValid(refreshToken, member)) {
                var accessToken = jwtService.generateToken(member);

                revokeAllMemberTokens(member);
                saveMemberToken(member, accessToken);

                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
}
