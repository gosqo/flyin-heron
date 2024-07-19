package com.vong.manidues.domain.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vong.manidues.global.exception.ErrorResponse;
import com.vong.manidues.domain.token.TokenUtility;
import com.vong.manidues.global.utility.JsonResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import static com.vong.manidues.domain.auth.AuthenticationFixture.MEMBER_ENTITY;
import static com.vong.manidues.web.HttpUtility.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class LogoutRestTest {
    private final TokenUtility tokenUtility;
    private final TestRestTemplate template;

    @Autowired
    public LogoutRestTest(TokenUtility tokenUtility, TestRestTemplate template) {
        this.tokenUtility = tokenUtility;
        this.template = template;
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
        final var accessToken = tokenUtility.buildToken(MEMBER_ENTITY);
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
        final var body = AuthenticationRequest.builder()
                .email("check@auth.io")
                .password("Password0")
                .build();
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
