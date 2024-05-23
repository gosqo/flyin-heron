package com.vong.manidues.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vong.manidues.exception.ErrorResponse;
import com.vong.manidues.web.HttpUtility;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class AuthenticationRestTests {
    private final TestRestTemplate template;

    @Autowired
    public AuthenticationRestTests(TestRestTemplate template) {
        this.template = template;
    }

    @Test
    void badCredentialsExceptionCheck() throws JsonProcessingException {
        var uri = "/api/v1/auth/authenticate";
        var body = AuthenticationRequest.builder()
                .email("wrong@email.ocm")
                .password("wrongPassword")
                .build();
        var request = HttpUtility.buildPostRequest(body, uri);

        var response = template.exchange(request, ErrorResponse.class);
        HttpUtility.logResponse(response);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void authSuccess() throws JsonProcessingException {
        var uri = "/api/v1/auth/authenticate";
        var body = AuthenticationRequest.builder()
                .email("check@auth.io")
                .password("Password0")
                .build();
        var request = HttpUtility.buildPostRequest(body, uri);

        var response = template.exchange(request, AuthenticationResponse.class);
        HttpUtility.logResponse(response);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}