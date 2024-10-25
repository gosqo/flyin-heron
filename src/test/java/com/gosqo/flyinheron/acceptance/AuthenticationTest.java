package com.gosqo.flyinheron.acceptance;

import com.gosqo.flyinheron.controller.AuthenticationController;
import com.gosqo.flyinheron.domain.Token;
import com.gosqo.flyinheron.domain.fixture.MemberFixture;
import com.gosqo.flyinheron.dto.auth.AuthenticationRequest;
import com.gosqo.flyinheron.dto.auth.AuthenticationResponse;
import com.gosqo.flyinheron.global.data.TestDataRemover;
import com.gosqo.flyinheron.global.exception.ErrorResponse;
import com.gosqo.flyinheron.global.utility.HeadersUtility;
import com.gosqo.flyinheron.global.utility.RequestCookie;
import com.gosqo.flyinheron.global.utility.RespondedCookie;
import com.gosqo.flyinheron.repository.MemberRepository;
import com.gosqo.flyinheron.repository.TokenRepository;
import com.gosqo.flyinheron.service.ClaimExtractor;
import com.gosqo.flyinheron.service.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;

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
    AuthenticationTest(
            TestRestTemplate template
            , MemberRepository memberRepository
            , TokenRepository tokenRepository
            , ClaimExtractor claimExtractor
            , JwtService jwtService
            , TestDataRemover remover
    ) {
        super(template, remover);
        this.memberRepository = memberRepository;
        this.tokenRepository = tokenRepository;
        this.claimExtractor = claimExtractor;
        this.jwtService = jwtService;
    }

    @BeforeEach
    void setUp() {
        member = memberRepository.save(buildMember());
    }

    @Test
    void auth_success_case() {
        final var body = AuthenticationRequest.builder()
                .email(member.getEmail())
                .password(MemberFixture.PASSWORD)
                .build();

        final var request = RequestEntity
                .post(AUTHENTICATE_URI)
                .body(body);

        final var response = template.exchange(request, AuthenticationResponse.class);

        final var cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);

        assertThat(cookies).isNotNull();

        final var refreshTokenCookie = RespondedCookie.extract(
                cookies
                , AuthenticationController.REFRESH_TOKEN_COOKIE_NAME
        );

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
    void bad_credential_response_Bad_Request() {
        // given
        final var body = AuthenticationRequest.builder()
                .email("wrong@email.ocm")
                .password("wrongPassword")
                .build();

        final var request = RequestEntity
                .post(AUTHENTICATE_URI)
                .body(body);

        // when
        final var response = template.exchange(request, ErrorResponse.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void if_requested_refresh_token_not_exist_on_database_response_Not_Found() {
        final var refreshToken = jwtService.generateRefreshToken(member);
        final var headers = HeadersUtility.buildHeadersContentTypeJson();

        final var cookieValue = RequestCookie.valueWith(
                AuthenticationController.REFRESH_TOKEN_COOKIE_NAME
                , refreshToken
        );

        headers.add(HttpHeaders.COOKIE, cookieValue);

        final var request = RequestEntity
                .post(REFRESH_TOKEN_URI)
                .headers(headers)
                .build();

        final var response = template.exchange(request, ErrorResponse.class);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Nested
    class If_expiry_of_refresh_token_left {

        @Test
        void _7_days_Reissue_refresh_token() {
            final var refreshTokenExpiresIn7Days = jwtService.generateRefreshToken(member, EXPIRATION_7_DAYS);
            final var expiry = claimExtractor.extractExpiration(refreshTokenExpiresIn7Days);
            final var tokenEntityExpiresIn7Days = Token.builder()
                    .token(refreshTokenExpiresIn7Days)
                    .member(member)
                    .expirationDate(expiry)
                    .build();

            tokenRepository.save(tokenEntityExpiresIn7Days);

            final var headers = HeadersUtility.buildHeadersContentTypeJson();

            final var cookieValue = RequestCookie.valueWith(
                    AuthenticationController.REFRESH_TOKEN_COOKIE_NAME
                    , refreshTokenExpiresIn7Days
            );

            headers.add(HttpHeaders.COOKIE, cookieValue);

            final var request = RequestEntity
                    .post(REFRESH_TOKEN_URI)
                    .headers(headers)
                    .build();

            final var response = template.exchange(request, AuthenticationResponse.class);

            final var cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);

            assertThat(cookies).isNotNull();

            String refreshTokenCookie = RespondedCookie.extract(
                    cookies
                    , AuthenticationController.REFRESH_TOKEN_COOKIE_NAME
            );

            assertThat(refreshTokenCookie).isNotNull();
            assertThat(refreshTokenCookie).contains("Max-Age");
            assertThat(refreshTokenCookie).contains("SameSite");
            assertThat(refreshTokenCookie).contains("HttpOnly");
            assertThat(refreshTokenCookie).contains("Path=/");

            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getAccessToken()).isNotBlank();
        }

        @Test
        void _8_days_Return_as_it_is() {
            final var refreshTokenExpiresIn8Days = jwtService.generateRefreshToken(member, EXPIRATION_8_DAYS);
            final var expiry = claimExtractor.extractExpiration(refreshTokenExpiresIn8Days);
            final var tokenEntityIn8Days = Token.builder()
                    .token(refreshTokenExpiresIn8Days)
                    .member(member)
                    .expirationDate(expiry)
                    .build();

            tokenRepository.save(tokenEntityIn8Days);

            final var headers = HeadersUtility.buildHeadersContentTypeJson();

            final var cookieValue = RequestCookie.valueWith(
                    AuthenticationController.REFRESH_TOKEN_COOKIE_NAME
                    , refreshTokenExpiresIn8Days
            );

            headers.add(HttpHeaders.COOKIE, cookieValue);

            final var request = RequestEntity
                    .post(REFRESH_TOKEN_URI)
                    .headers(headers)
                    .build();

            final var response = template.exchange(request, AuthenticationResponse.class);

            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getAccessToken()).isNotNull();
            assertThat(response.getHeaders().get(HttpHeaders.SET_COOKIE)).isNull();
        }
    }
}
