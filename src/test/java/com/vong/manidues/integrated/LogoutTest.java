package com.vong.manidues.integrated;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vong.manidues.domain.fixture.MemberFixture;
import com.vong.manidues.dto.JsonResponse;
import com.vong.manidues.dto.auth.AuthenticationResponse;
import com.vong.manidues.global.exception.ErrorResponse;
import com.vong.manidues.repository.BoardRepository;
import com.vong.manidues.repository.CommentRepository;
import com.vong.manidues.repository.MemberRepository;
import com.vong.manidues.repository.TokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;

import static com.vong.manidues.web.HttpUtility.buildPostHeadersWithToken;
import static com.vong.manidues.web.HttpUtility.buildPostRequestEntity;
import static org.assertj.core.api.Assertions.assertThat;

class LogoutTest extends SpringBootTestBase {
    private static final String LOGOUT_URI = "/api/v1/auth/logout";
    private static final String LOGIN_URI = "/api/v1/auth/authenticate";

    private final TokenUtility tokenUtility;

    @Autowired
    public LogoutTest(
            MemberRepository memberRepository,
            TokenRepository tokenRepository,
            BoardRepository boardRepository,
            CommentRepository commentRepository,
            TestRestTemplate template,
            TokenUtility tokenUtility
    ) {
        super(memberRepository, tokenRepository, boardRepository, commentRepository, template);
        this.tokenUtility = tokenUtility;
    }

    @BeforeEach
    void setUp() {
        initMember();
    }

    @Test
    public void logout_without_auth_header_response_Bad_Request() throws JsonProcessingException {
        final var request = buildPostRequestEntity(null, LOGOUT_URI);
        final var response = template.exchange(request, ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void logout_with_token_not_exist_on_database_response_Bad_Request() throws JsonProcessingException {
        final var accessToken = tokenUtility.buildToken(member);
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
