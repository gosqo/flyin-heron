package com.vong.manidues.integrated;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vong.manidues.domain.Token;
import com.vong.manidues.domain.fixture.MemberFixture;
import com.vong.manidues.dto.auth.AuthenticationRequest;
import com.vong.manidues.dto.auth.AuthenticationResponse;
import com.vong.manidues.global.exception.ErrorResponse;
import com.vong.manidues.global.utility.HttpUtility;
import com.vong.manidues.repository.MemberRepository;
import com.vong.manidues.repository.TokenRepository;
import com.vong.manidues.service.ClaimExtractor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;

import static com.vong.manidues.global.utility.HttpUtility.*;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class AuthenticationTest extends SpringBootTestBase {
    private static final long EXPIRATION_7_DAYS = 604800000L;
    private static final long EXPIRATION_8_DAYS = 691200000L;
    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;
    private final ClaimExtractor claimExtractor;
    private final TestTokenBuilder tokenBuilder;
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Autowired
    public AuthenticationTest(
            TestRestTemplate template,
            MemberRepository memberRepository,
            TokenRepository tokenRepository,
            ClaimExtractor claimExtractor,
            TestTokenBuilder tokenBuilder
    ) {
        super(template);
        this.memberRepository = memberRepository;
        this.tokenRepository = tokenRepository;
        this.claimExtractor = claimExtractor;
        this.tokenBuilder = tokenBuilder;
    }

    @Override
    void initData() {
        member = memberRepository.save(buildMember());
    }

    @Override
    @BeforeEach
    void setUp() {
        initData();
    }

    @Override
    @AfterEach
    void tearDown() {
        tokenRepository.deleteAll();
        memberRepository.deleteAll();
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
        memberRepository.findAll().forEach(each -> log.info("{}", each.getEmail()));

        final var member = memberRepository.findByEmail(MemberFixture.EMAIL).orElseThrow();
        final var refreshTokenIn7Days = tokenBuilder.buildToken(member, EXPIRATION_7_DAYS);
        final var bearerRefreshTokenIn7Days = "Bearer " + refreshTokenIn7Days;

        final var uri = "/api/v1/auth/refresh-token";
        final var headers = buildPostHeadersWithBearerToken(bearerRefreshTokenIn7Days);
        final var request = buildPostRequestEntity(headers, null, uri);

        final var response = template.exchange(request, ErrorResponse.class);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Nested
    class If_expiry_of_refresh_token_left {

        @Test
        void _7_days_Reissue_refresh_token() throws JsonProcessingException {
            final var member = memberRepository.findByEmail(MemberFixture.EMAIL).orElseThrow();
            final var refreshTokenExpiresIn7Days = tokenBuilder.buildToken(member, EXPIRATION_7_DAYS);
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
        }

        @Test
        void _8_days_Return_as_it_is() throws JsonProcessingException {
            final var member = memberRepository.findByEmail(MemberFixture.EMAIL).orElseThrow();
            final var refreshTokenExpiresIn8Days = tokenBuilder.buildToken(member, EXPIRATION_8_DAYS);
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
        }
    }
}