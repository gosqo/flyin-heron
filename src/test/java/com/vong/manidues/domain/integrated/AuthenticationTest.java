package com.vong.manidues.domain.integrated;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vong.manidues.domain.auth.AuthenticationRequest;
import com.vong.manidues.domain.auth.AuthenticationResponse;
import com.vong.manidues.domain.board.BoardRepository;
import com.vong.manidues.domain.comment.CommentRepository;
import com.vong.manidues.domain.member.MemberFixture;
import com.vong.manidues.domain.member.MemberRepository;
import com.vong.manidues.domain.token.ClaimExtractor;
import com.vong.manidues.domain.token.Token;
import com.vong.manidues.domain.token.TokenRepository;
import com.vong.manidues.global.exception.ErrorResponse;
import com.vong.manidues.web.HttpUtility;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.vong.manidues.web.HttpUtility.*;
import static org.assertj.core.api.Assertions.assertThat;

public class AuthenticationTest extends SpringBootTestBase {
    private final ClaimExtractor claimExtractor;
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Autowired
    public AuthenticationTest(
            MemberRepository memberRepository,
            TokenRepository tokenRepository,
            BoardRepository boardRepository,
            CommentRepository commentRepository,
            TestRestTemplate template,
            ClaimExtractor claimExtractor
    ) {
        super(memberRepository, tokenRepository, boardRepository, commentRepository, template);
        this.claimExtractor = claimExtractor;
    }

    @BeforeEach
    void setUp() {
        initMember();
    }

    @Test
    @DisplayName("Bad Credential response Bad Request.")
    void badCredentialsExceptionCheck() throws JsonProcessingException {
        // given
        var uri = "/api/v1/auth/authenticate";
        var body = AuthenticationRequest.builder()
                .email("wrong@email.ocm")
                .password("wrongPassword")
                .build();
        var request = HttpUtility.buildPostRequest(body, uri);

        // when
        var response = template.exchange(request, ErrorResponse.class);
        HttpUtility.logResponse(response);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void authSuccess() throws JsonProcessingException {
        var uri = "/api/v1/auth/authenticate";
        var body = MemberFixture.AUTH_REQUEST;
        var request = HttpUtility.buildPostRequest(body, uri);

        var response = template.exchange(request, AuthenticationResponse.class);
        HttpUtility.logResponse(response);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void refreshTokenIn7Days() throws JsonProcessingException {
        final var expirationIn7Days = 604800000L; // 1000 * 60 * 60 * 24 * 7 (expired in 7 days)
        final var member = memberRepository.findByEmail(MemberFixture.EMAIL).orElseThrow();
        final var refreshTokenIn7Days = buildToken(new HashMap<>(), member, expirationIn7Days);
        final var bearerRefreshTokenIn7Days = "Bearer " + refreshTokenIn7Days;
        final var tokenEntityIn7Days = Token.builder().token(refreshTokenIn7Days).member(member).expirationDate(claimExtractor.extractExpiration(refreshTokenIn7Days)).build();

        tokenRepository.save(tokenEntityIn7Days);

        final var uri = "/api/v1/auth/refresh-token";
        final var headers = buildPostHeadersWithAuth(bearerRefreshTokenIn7Days);
        final var request = buildPostRequest(headers, null, uri);

        final var response = template.exchange(request, AuthenticationResponse.class);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getAccessToken()).isNotNull();
        assertThat(response.getBody().getRefreshToken()).isNotEqualTo(refreshTokenIn7Days);

        logResponse(response);
    }

    @Test
    void refreshTokenIn8Days() throws JsonProcessingException {
        final var expirationIn8Days = 691200000L; // 1000 * 60 * 60 * 24 * 8 (expired in 8 days)
        final var member = memberRepository.findByEmail(MemberFixture.EMAIL).orElseThrow();
        final var refreshTokenIn8Days = buildToken(new HashMap<>(), member, expirationIn8Days);
        final var bearerRefreshTokenIn7Days = "Bearer " + refreshTokenIn8Days;
        final var tokenEntityIn8Days = Token.builder().token(refreshTokenIn8Days).member(member).expirationDate(claimExtractor.extractExpiration(refreshTokenIn8Days)).build();

        tokenRepository.save(tokenEntityIn8Days);

        final var uri = "/api/v1/auth/refresh-token";
        final var headers = buildPostHeadersWithAuth(bearerRefreshTokenIn7Days);
        final var request = buildPostRequest(headers, null, uri);

        final var response = template.exchange(request, AuthenticationResponse.class);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getAccessToken()).isNotNull();
        assertThat(response.getBody().getRefreshToken()).isEqualTo(refreshTokenIn8Days);

        logResponse(response);
    }

    @Test
    void refreshTokenNotExistOnDatabase() throws JsonProcessingException {
        final var expirationIn7Days = 604800000L; // 1000 * 60 * 60 * 24 * 7 (expired in 7 days)
        final var member = memberRepository.findByEmail(MemberFixture.EMAIL).orElseThrow();
        final var refreshTokenIn7Days = buildToken(new HashMap<>(), member, expirationIn7Days);
        final var bearerRefreshTokenIn7Days = "Bearer " + refreshTokenIn7Days;

        final var uri = "/api/v1/auth/refresh-token";
        final var headers = buildPostHeadersWithAuth(bearerRefreshTokenIn7Days);
        final var request = buildPostRequest(headers, null, uri);

        final var response = template.exchange(request, ErrorResponse.class);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        logResponse(response);
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", memberRepository.findByEmail(userDetails.getUsername()).orElseThrow().getId());

        return Jwts.builder()
                .setClaims(extraClaims)
                .addClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}