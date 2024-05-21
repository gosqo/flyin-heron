package com.vong.manidues.auth;

import com.vong.manidues.web.HttpUtility;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class LogoutExceptionTests {
    private final TestRestTemplate template;

    @Autowired
    public LogoutExceptionTests(TestRestTemplate template) {
        this.template = template;
    }

    @Test
    public void logoutWithoutAuthHeader() {
        var request = HttpUtility.DEFAULT_HTTP_ENTITY;
        var response = template.exchange(
                "/api/v1/auth/logout"
                , HttpMethod.POST
                , request
                , Object.class
        );

        log.info("result: {}\n{}\n{}"
                , response.getStatusCode()
                , response.getHeaders()
                , response.getBody()
        );
    }

}
