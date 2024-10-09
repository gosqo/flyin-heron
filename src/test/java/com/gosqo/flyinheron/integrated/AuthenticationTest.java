package com.gosqo.flyinheron.integrated;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gosqo.flyinheron.domain.Token;
import com.gosqo.flyinheron.domain.fixture.MemberFixture;
import com.gosqo.flyinheron.dto.auth.AuthenticationRequest;
import com.gosqo.flyinheron.dto.auth.AuthenticationResponse;
import com.gosqo.flyinheron.global.exception.ErrorResponse;
import com.gosqo.flyinheron.global.utility.HttpUtility;
import com.gosqo.flyinheron.global.utility.RequestCookie;
import com.gosqo.flyinheron.global.utility.RespondedCookie;
import com.gosqo.flyinheron.repository.MemberRepository;
import com.gosqo.flyinheron.repository.TokenRepository;
import com.gosqo.flyinheron.service.ClaimExtractor;
import com.gosqo.flyinheron.service.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import static com.gosqo.flyinheron.controller.AuthenticationController.REFRESH_TOKEN_COOKIE_NAME;
import static com.gosqo.flyinheron.global.utility.HttpUtility.buildDefaultPostHeaders;
import static com.gosqo.flyinheron.global.utility.HttpUtility.buildPostRequestEntity;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class AuthenticationTest extends SpringBootTestBase {
    private static final long EXPIRATION_7_DAYS = 604800000L;
    private static final long EXPIRATION_8_DAYS = 691200000L;
    private static final String REFRESH_TOKEN_URI = "/api/v1/auth/refresh-token";
    private static final String AUTHENTICATE_URI = "/api/v1/auth/authenticate";
    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;
    private final ClaimExtractor claimExtractor;
    private final JwtService jwtService;

    @Autowired
    public AuthenticationTest(
            TestRestTemplate template,
            MemberRepository memberRepository,
            TokenRepository tokenRepository,
            ClaimExtractor claimExtractor,
            JwtService jwtService
    ) {
        super(template);
        this.memberRepository = memberRepository;
        this.tokenRepository = tokenRepository;
        this.claimExtractor = claimExtractor;
        this.jwtService = jwtService;
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
        final var body = AuthenticationRequest.builder()
                .email(member.getEmail())
                .password(MemberFixture.PASSWORD)
                .build();

        final var request = HttpUtility.buildPostRequestEntity(body, AUTHENTICATE_URI);
        final var response = template.exchange(request, AuthenticationResponse.class);

        final var cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);

        assertThat(cookies).isNotNull();

        final var refreshTokenCookie = RespondedCookie.extract(cookies, REFRESH_TOKEN_COOKIE_NAME);

        assertThat(refreshTokenCookie).isNotNull();
        assertThat(refreshTokenCookie).contains("Max-Age");
        assertThat(refreshTokenCookie).contains("SameSite");
        assertThat(refreshTokenCookie).contains("HttpOnly");
        assertThat(refreshTokenCookie).contains("Path=/");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getAccessToken()).isNotNull();
    }

    @Test
    void bad_credential_response_Bad_Request() throws JsonProcessingException {
        // given
        final var body = AuthenticationRequest.builder()
                .email("wrong@email.ocm")
                .password("wrongPassword")
                .build();
        final var request = HttpUtility.buildPostRequestEntity(body, AUTHENTICATE_URI);

        // when
        final var response = template.exchange(request, ErrorResponse.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void if_requested_refresh_token_not_exist_on_database_response_Bad_Request() throws JsonProcessingException {
        final var refreshToken = jwtService.generateRefreshToken(member);
        final var headers = buildDefaultPostHeaders();

        final var cookieValue = RequestCookie.valueWith(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
        headers.add(HttpHeaders.COOKIE, cookieValue);

        final var request = buildPostRequestEntity(headers, null, REFRESH_TOKEN_URI);
        final var response = template.exchange(request, ErrorResponse.class);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Nested
    class If_expiry_of_refresh_token_left {

        @Test
        void _7_days_Reissue_refresh_token() throws JsonProcessingException {
            final var refreshTokenExpiresIn7Days = jwtService.generateRefreshToken(member, EXPIRATION_7_DAYS);
            final var expiry = claimExtractor.extractExpiration(refreshTokenExpiresIn7Days);
            final var tokenEntityExpiresIn7Days = Token.builder()
                    .token(refreshTokenExpiresIn7Days)
                    .member(member)
                    .expirationDate(expiry)
                    .build();

            tokenRepository.save(tokenEntityExpiresIn7Days);

            final var headers = buildDefaultPostHeaders();

            final var cookieValue = RequestCookie.valueWith(REFRESH_TOKEN_COOKIE_NAME, refreshTokenExpiresIn7Days);
            headers.add(HttpHeaders.COOKIE, cookieValue);

            final var request = buildPostRequestEntity(headers, null, REFRESH_TOKEN_URI);
            final var response = template.exchange(request, AuthenticationResponse.class);

            final var cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);

            assertThat(cookies).isNotNull();

            String refreshTokenCookie = RespondedCookie.extract(cookies, REFRESH_TOKEN_COOKIE_NAME);

            assertThat(refreshTokenCookie).isNotNull();
            assertThat(refreshTokenCookie).contains("Max-Age");
            assertThat(refreshTokenCookie).contains("SameSite");
            assertThat(refreshTokenCookie).contains("HttpOnly");
            assertThat(refreshTokenCookie).contains("Path=/");

            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getAccessToken()).isNotBlank();
        }

        @Test
        void _8_days_Return_as_it_is() throws JsonProcessingException {
            final var refreshTokenExpiresIn8Days = jwtService.generateRefreshToken(member, EXPIRATION_8_DAYS);
            final var expiry = claimExtractor.extractExpiration(refreshTokenExpiresIn8Days);
            final var tokenEntityIn8Days = Token.builder()
                    .token(refreshTokenExpiresIn8Days)
                    .member(member)
                    .expirationDate(expiry)
                    .build();

            tokenRepository.save(tokenEntityIn8Days);

            final var headers = buildDefaultPostHeaders();

            final var cookieValue = RequestCookie.valueWith(REFRESH_TOKEN_COOKIE_NAME, refreshTokenExpiresIn8Days);
            headers.add(HttpHeaders.COOKIE, cookieValue);

            final var request = buildPostRequestEntity(headers, null, REFRESH_TOKEN_URI);
            final var response = template.exchange(request, AuthenticationResponse.class);

            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getAccessToken()).isNotNull();
            assertThat(response.getHeaders().get(HttpHeaders.SET_COOKIE)).isNull();
        }
    }
}