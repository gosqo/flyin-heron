package com.vong.manidues.domain.integrated;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vong.manidues.domain.auth.AuthenticationResponse;
import com.vong.manidues.domain.board.BoardRepository;
import com.vong.manidues.domain.comment.CommentRepository;
import com.vong.manidues.domain.member.MemberFixture;
import com.vong.manidues.domain.member.MemberRepository;
import com.vong.manidues.domain.token.TokenRepository;
import com.vong.manidues.domain.token.TokenUtility;
import com.vong.manidues.global.exception.ErrorResponse;
import com.vong.manidues.global.utility.JsonResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;

import static com.vong.manidues.web.HttpUtility.*;
import static org.assertj.core.api.Assertions.assertThat;

public class LogoutTest extends SpringBootTestBase {
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
    public void logoutWithoutAuthHeader() throws JsonProcessingException {
        final var request = buildPostRequest(null, "/api/v1/auth/logout");
        final var response = template.exchange(request, ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        logResponse(response);
    }

    @Test
    public void logoutWithAuthHeaderNotExistOnDatabase() throws JsonProcessingException {
        final var accessToken = tokenUtility.buildToken(member);
        final var bearerAccessToken = "Bearer " + accessToken;
        final var headers = buildDefaultHeaders();
        headers.add("Authorization", bearerAccessToken);
        final var request = buildPostRequest(headers, null, "/api/v1/auth/logout");
        final var response = template.exchange(request, JsonResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        logResponse(response);
    }

    @Test
    public void loginThenLogout() throws JsonProcessingException {
        final var loginURI = "/api/v1/auth/authenticate";
        final var body = MemberFixture.AUTH_REQUEST;
        final var loginRequest = buildPostRequest(body, loginURI);
        final var loginResponse = template.exchange(loginRequest, AuthenticationResponse.class);
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResponse.getBody()).isNotNull();
        logResponse(loginResponse);

        final var logoutURI = "/api/v1/auth/logout";
        final var refreshToken = "Bearer " + loginResponse.getBody().getRefreshToken();
        final var logoutHeaders = buildPostHeaders("Authorization", refreshToken);
        final var logoutRequest = buildPostRequest(logoutHeaders, null, logoutURI);
        final var logoutResponse = template.exchange(logoutRequest, JsonResponse.class);
        assertThat(logoutResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        logResponse(logoutResponse);
    }
}
