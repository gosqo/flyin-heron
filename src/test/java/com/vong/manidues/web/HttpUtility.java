package com.vong.manidues.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vong.manidues.token.TokenUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
@RequiredArgsConstructor
@Slf4j
public class HttpUtility {

    private final TokenUtility tokenUtility;

    public static final HttpHeaders DEFAULT_HTTP_GET_HEADERS = new HttpHeaders();
    public static final HttpHeaders DEFAULT_HTTP_POST_HEADERS = DEFAULT_HTTP_GET_HEADERS;

    public static final HttpEntity<String> DEFAULT_HTTP_ENTITY = new HttpEntity<>(HttpUtility.DEFAULT_HTTP_GET_HEADERS);

    static {
        DEFAULT_HTTP_GET_HEADERS.add("Connection", "Keep-Alive");
        DEFAULT_HTTP_GET_HEADERS.add("User-Agent", "Mozilla");
        DEFAULT_HTTP_POST_HEADERS.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
    }

    public static RequestEntity<String> postRequestOf(Object body, String uri)
            throws JsonProcessingException {
        return new RequestEntity<>(
                getMappedBody(body)
                , DEFAULT_HTTP_POST_HEADERS
                , HttpMethod.POST
                , URI.create(uri)
        );
    }

    public static RequestEntity<String> getRequestOf(String uri) {
        return new RequestEntity<>(
                DEFAULT_HTTP_GET_HEADERS
                , HttpMethod.GET
                , URI.create(uri)
        );
    }

    private static <T> String getMappedBody(Object body)
            throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(body);
    }

    public HttpHeaders headersWithAuthorization(Long memberId) {
        HttpHeaders headers = new HttpHeaders(DEFAULT_HTTP_GET_HEADERS);

        headers.add(
                "Authorization"
                , tokenUtility.issueAccessTokenOnTest(memberId)
        );

        return headers;
    }

    public static <T> void logResponse(ResponseEntity<T> response) {
        log.info(response.getBody() == null
                        ? "response: {}\n{}"
                        : "response: {}\n{}\n{}"
                , response.getStatusCode()
                , response.getHeaders()
                , response.getBody()
        );
    }
}
