package com.vong.manidues.integrated;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vong.manidues.domain.Token;
import com.vong.manidues.domain.fixture.MemberFixture;
import com.vong.manidues.dto.auth.AuthenticationRequest;
import com.vong.manidues.dto.auth.AuthenticationResponse;
import com.vong.manidues.global.exception.ErrorResponse;
import com.vong.manidues.repository.BoardRepository;
import com.vong.manidues.repository.CommentRepository;
import com.vong.manidues.repository.MemberRepository;
import com.vong.manidues.repository.TokenRepository;
import com.vong.manidues.service.ClaimExtractor;
import com.vong.manidues.web.HttpUtility;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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

class AuthenticationTest extends SpringBootTestBase {
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
    void auth_success_case() throws JsonProcessingException {
        var uri = "/api/v1/auth/authenticate";
        var body = MemberFixture.AUTH_REQUEST;
        var request = HttpUtility.buildPostRequestEntity(body, uri);

        var response = template.exchange(request, AuthenticationResponse.class);
        HttpUtility.logResponse(response);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void bad_credential_response_Bad_Request() throws JsonProcessingException {
        // given
        var uri = "/api/v1/auth/authenticate";
        var body = AuthenticationRequest.builder()
                .email("wrong@email.ocm")
                .password("wrongPassword")
                .build();
        var request = HttpUtility.buildPostRequestEntity(body, uri);

        // when
        var response = template.exchange(request, ErrorResponse.class);
        HttpUtility.logResponse(response);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void if_requested_refresh_token_not_exist_on_database_response_Bad_Request() throws JsonProcessingException {
        final var expirationIn7Days = 604800000L; // 1000 * 60 * 60 * 24 * 7 (expired in 7 days)
        final var member = memberRepository.findByEmail(MemberFixture.EMAIL).orElseThrow();
        final var refreshTokenIn7Days = buildToken(new HashMap<>(), member, expirationIn7Days);
        final var bearerRefreshTokenIn7Days = "Bearer " + refreshTokenIn7Days;

        final var uri = "/api/v1/auth/refresh-token";
        final var headers = buildPostHeadersWithBearerToken(bearerRefreshTokenIn7Days);
        final var request = buildPostRequestEntity(headers, null, uri);

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

    @Nested
    class If_expiry_of_refresh_token_left {

        @Test
        void _7_days_Reissue_refresh_token() throws JsonProcessingException {
            final var expirationIn7Days = 604800000L; // 1000 * 60 * 60 * 24 * 7 (expired in 7 days)
            final var member = memberRepository.findByEmail(MemberFixture.EMAIL).orElseThrow();
            final var refreshTokenExpiresIn7Days = buildToken(new HashMap<>(), member, expirationIn7Days);
            final var expiry = claimExtractor.extractExpiration(refreshTokenExpiresIn7Days);
            final var tokenEntityExpiresIn7Days = Token.builder()
                    .token(refreshTokenExpiresIn7Days)
                    .member(member)
                    .expirationDate(expiry)
                    .build();

            tokenRepository.save(tokenEntityExpiresIn7Days);

            final var uri = "/api/v1/auth/refresh-token";
            final var headers = buildPostHeadersWithToken(refreshTokenExpiresIn7Days);
            final var request = buildPostRequestEntity(headers, null, uri);

            final var response = template.exchange(request, AuthenticationResponse.class);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getAccessToken()).isNotNull();
            assertThat(response.getBody().getRefreshToken()).isNotEqualTo(refreshTokenExpiresIn7Days);

            logResponse(response);
        }

        @Test
        void _8_days_Return_as_it_is() throws JsonProcessingException {
            final var expirationIn8Days = 691200000L; // 1000 * 60 * 60 * 24 * 8 (expired in 8 days)
            final var member = memberRepository.findByEmail(MemberFixture.EMAIL).orElseThrow();
            final var refreshTokenExpiresIn8Days = buildToken(new HashMap<>(), member, expirationIn8Days);
            final var expiry = claimExtractor.extractExpiration(refreshTokenExpiresIn8Days);
            final var tokenEntityIn8Days = Token.builder()
                    .token(refreshTokenExpiresIn8Days)
                    .member(member)
                    .expirationDate(expiry)
                    .build();

            tokenRepository.save(tokenEntityIn8Days);

            final var uri = "/api/v1/auth/refresh-token";
            final var headers = buildPostHeadersWithToken(refreshTokenExpiresIn8Days);
            final var request = buildPostRequestEntity(headers, null, uri);

            final var response = template.exchange(request, AuthenticationResponse.class);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getAccessToken()).isNotNull();
            assertThat(response.getBody().getRefreshToken()).isEqualTo(refreshTokenExpiresIn8Days);

            logResponse(response);
        }
    }
}