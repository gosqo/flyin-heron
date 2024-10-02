package com.gosqo.flyinheron.integrated;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gosqo.flyinheron.domain.fixture.MemberFixture;
import com.gosqo.flyinheron.dto.JsonResponse;
import com.gosqo.flyinheron.dto.auth.AuthenticationResponse;
import com.gosqo.flyinheron.global.exception.ErrorResponse;
import com.gosqo.flyinheron.repository.MemberRepository;
import com.gosqo.flyinheron.repository.TokenRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;

import static com.gosqo.flyinheron.global.utility.HttpUtility.buildPostHeadersWithToken;
import static com.gosqo.flyinheron.global.utility.HttpUtility.buildPostRequestEntity;
import static org.assertj.core.api.Assertions.assertThat;

class LogoutTest extends SpringBootTestBase {
    private static final String LOGOUT_URI = "/api/v1/auth/logout";
    private static final String LOGIN_URI = "/api/v1/auth/authenticate";

    private final TestTokenBuilder tokenBuilder;
    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;

    @Autowired
    public LogoutTest(
            TestRestTemplate template,
            TestTokenBuilder tokenBuilder,
            MemberRepository memberRepository,
            TokenRepository tokenRepository
    ) {
        super(template);
        this.tokenBuilder = tokenBuilder;
        this.memberRepository = memberRepository;
        this.tokenRepository = tokenRepository;
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
    public void logout_without_auth_header_response_Bad_Request() throws JsonProcessingException {
        final var request = buildPostRequestEntity(null, LOGOUT_URI);
        final var response = template.exchange(request, ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void logout_with_token_not_exist_on_database_response_Bad_Request() throws JsonProcessingException {
        final var accessToken = tokenBuilder.buildToken(member);
        final var headers = buildPostHeadersWithToken(accessToken);
        final var request = buildPostRequestEntity(headers, null, LOGOUT_URI);
        final var response = template.exchange(request, JsonResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void login_then_logout_response_Ok() throws JsonProcessingException {
        final var body = MemberFixture.AUTH_REQUEST;
        final var loginRequest = buildPostRequestEntity(body, LOGIN_URI);
        final var loginResponse = template.exchange(loginRequest, AuthenticationResponse.class);

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResponse.getBody()).isNotNull();
        assertThat(loginResponse.getBody().getAccessToken()).isNotNull();
        assertThat(loginResponse.getBody().getRefreshToken()).isNotNull();

        final var refreshToken = loginResponse.getBody().getRefreshToken();
        final var logoutHeaders = buildPostHeadersWithToken(refreshToken);
        final var logoutRequest = buildPostRequestEntity(logoutHeaders, null, LOGOUT_URI);
        final var logoutResponse = template.exchange(logoutRequest, JsonResponse.class);

        assertThat(logoutResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
