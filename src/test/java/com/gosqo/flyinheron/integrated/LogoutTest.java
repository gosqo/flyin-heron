package com.gosqo.flyinheron.integrated;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gosqo.flyinheron.domain.fixture.MemberFixture;
import com.gosqo.flyinheron.dto.JsonResponse;
import com.gosqo.flyinheron.dto.auth.AuthenticationResponse;
import com.gosqo.flyinheron.global.data.TestDataRemover;
import com.gosqo.flyinheron.global.exception.ErrorResponse;
import com.gosqo.flyinheron.global.utility.RequestCookie;
import com.gosqo.flyinheron.global.utility.RespondedCookie;
import com.gosqo.flyinheron.repository.MemberRepository;
import com.gosqo.flyinheron.repository.TokenRepository;
import com.gosqo.flyinheron.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import static com.gosqo.flyinheron.controller.AuthenticationController.REFRESH_TOKEN_COOKIE_NAME;
import static com.gosqo.flyinheron.global.utility.HttpUtility.buildDefaultPostHeaders;
import static com.gosqo.flyinheron.global.utility.HttpUtility.buildPostRequestEntity;
import static org.assertj.core.api.Assertions.assertThat;

class LogoutTest extends SpringBootTestBase {
    private static final String LOGOUT_URI = "/api/v1/auth/logout";
    private static final String LOGIN_URI = "/api/v1/auth/authenticate";

    private final JwtService jwtService;
    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;

    @Autowired
    public LogoutTest(
            TestRestTemplate template
            , JwtService jwtService
            , MemberRepository memberRepository
            , TokenRepository tokenRepository
            , TestDataRemover remover
    ) {
        super(template, remover);
        this.jwtService = jwtService;
        this.memberRepository = memberRepository;
        this.tokenRepository = tokenRepository;
    }

    @BeforeEach
    void setUp() {
        member = memberRepository.save(buildMember());
    }

    @Test
    public void logout_without_auth_header_response_Bad_Request() throws JsonProcessingException {
        // given
        final var request = buildPostRequestEntity(null, LOGOUT_URI);

        // when
        final var response = template.exchange(request, ErrorResponse.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void logout_with_token_not_exist_on_database_response_Bad_Request() throws JsonProcessingException {
        // given
        final var refreshToken = jwtService.generateRefreshToken(member);
        final var headers = buildDefaultPostHeaders();
        final var cookieValue = RequestCookie.valueWith(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
        headers.add(HttpHeaders.COOKIE, cookieValue);
        final var request = buildPostRequestEntity(headers, null, LOGOUT_URI);

        // when
        final var response = template.exchange(request, ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void login_then_logout_response_Ok() throws JsonProcessingException {
        // given
        final var body = MemberFixture.AUTH_REQUEST;
        final var loginRequest = buildPostRequestEntity(body, LOGIN_URI);
        final var loginResponse = template.exchange(loginRequest, AuthenticationResponse.class);

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResponse.getBody()).isNotNull();
        assertThat(loginResponse.getBody().getAccessToken()).isNotNull();

        final var loginResponseHeaders = loginResponse.getHeaders().get(HttpHeaders.SET_COOKIE);
        assertThat(loginResponseHeaders).isNotNull();

        final var refreshTokenCookie = RespondedCookie
                .extract(loginResponseHeaders, REFRESH_TOKEN_COOKIE_NAME);
        assertThat(refreshTokenCookie).isNotNull();

        final var refreshToken = RespondedCookie.getCookieValue(refreshTokenCookie);

        final var logoutHeaders = buildDefaultPostHeaders();
        final var requestCookieValue = RequestCookie.valueWith(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
        logoutHeaders.add(HttpHeaders.COOKIE, requestCookieValue);

        final var logoutRequest = buildPostRequestEntity(logoutHeaders, null, LOGOUT_URI);

        // when
        final var logoutResponse = template.exchange(logoutRequest, JsonResponse.class);

        // then
        assertThat(logoutResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
