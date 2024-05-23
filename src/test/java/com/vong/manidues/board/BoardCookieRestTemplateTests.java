package com.vong.manidues.board;

import com.vong.manidues.board.dto.BoardGetResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.ArrayList;
import java.util.List;

import static com.vong.manidues.web.HttpUtility.DEFAULT_GET_HEADERS;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class BoardCookieRestTemplateTests {

    @Autowired
    TestRestTemplate template;

    @Test
    public void initializeBbvCookie() throws Exception {
        HttpHeaders requestHeaders = new HttpHeaders(DEFAULT_GET_HEADERS);
        HttpEntity<String> request = new HttpEntity<>(requestHeaders);

        ResponseEntity<BoardGetResponse> response = template.exchange(
                "/api/v1/board/1"
                , HttpMethod.GET
                , request
                , BoardGetResponse.class
        );

        log.info("\n{}\n{}\n{}"
                , response.getStatusCode()
                , response.getHeaders()
                , response.getBody()
        );

        assertThat(response.getHeaders().get("Set-Cookie")).isNotNull();
        assertThat(response.getHeaders().get("Set-Cookie").get(0)).startsWith("bbv");
    }

    @Test
    public void canSetCookie() throws Exception {
        // first
        HttpHeaders requestHeaders = new HttpHeaders(DEFAULT_GET_HEADERS);
        requestHeaders.add("Cookie", "bbv=1");
        List<String> requestCookies = requestHeaders.get("Cookie");

        HttpEntity<String> firstRequest = new HttpEntity<>(requestHeaders);
        String firstUri = "/api/v1/board/2";

        ResponseEntity<BoardGetResponse> firstResponse = template.exchange(
                firstUri
                , HttpMethod.GET
                , firstRequest
                , BoardGetResponse.class
        );

        List<String> firstResponseCookies = firstResponse.getHeaders().get("Set-Cookie");

        assert requestCookies != null;
        requestCookies.removeIf(item -> item.startsWith("bbv"));

        assert firstResponseCookies != null;
        HttpEntity<String> secondRequest =
                httpEntityWithCookiesFromServer(firstResponseCookies, requestHeaders);
        String secondUri = "/api/v1/board/3";

        ResponseEntity<BoardGetResponse> secondResponse = template.exchange(
                secondUri
                , HttpMethod.GET
                , secondRequest
                , BoardGetResponse.class
        );

        List<String> secondResponseCookies = secondResponse.getHeaders().get("Set-Cookie");

        assertThat(secondResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(secondResponseCookies).isNotNull();
        assertThat(secondResponseCookies.get(0)).startsWith("bbv=1/2/3");
    }

    private HttpEntity<String> httpEntityWithCookiesFromServer(
            List<String> cookiesFromServer
            , HttpHeaders requestHeaders
    ) {
        List<String> nextRequestHeaderCookieValue = new ArrayList<>();

        for (String cookie : cookiesFromServer) {
            String[] cookiePairs = cookie.split(";");
            nextRequestHeaderCookieValue.add(cookiePairs[0]);
        }

        requestHeaders.addAll("Cookie", nextRequestHeaderCookieValue);

        return new HttpEntity<>(requestHeaders);
    }
}
