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
    public static final HttpHeaders DEFAULT_GET_HEADERS = new HttpHeaders();
    public static final HttpHeaders DEFAULT_POST_HEADERS = new HttpHeaders();
    public static final HttpEntity<String> DEFAULT_HTTP_ENTITY = new HttpEntity<>(HttpUtility.DEFAULT_GET_HEADERS);

    static {
        defaultGetHeaders(DEFAULT_GET_HEADERS);
        defaultPostHeaders(DEFAULT_POST_HEADERS);
    }

    private final TokenUtility tokenUtility;

    private static void defaultHeaders(HttpHeaders headers) {
        headers.add("Connection", "Keep-Alive");
        headers.add("User-Agent", "Mozilla");
    }

    private static void defaultGetHeaders(HttpHeaders headers) {
        defaultHeaders(headers);
    }

    private static void defaultPostHeaders(HttpHeaders headers) {
        defaultHeaders(headers);
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
    }

    public static HttpHeaders buildDefaultHeaders() {
        var headers = new HttpHeaders();
        defaultHeaders(headers);
        return headers;
    }

    public static HttpHeaders buildDefaultPostHeaders() {
        var headers = buildDefaultHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        return headers;
    }

    public static RequestEntity<String> buildPostRequest(Object body, String uri)
            throws JsonProcessingException {
        return new RequestEntity<>(
                getMappedBody(body)
                , buildDefaultPostHeaders()
                , HttpMethod.POST
                , URI.create(uri)
        );
    }

    public static RequestEntity<String> buildPostRequest(HttpHeaders httpHeaders, Object body, String uri)
            throws JsonProcessingException {
        return new RequestEntity<>(
                getMappedBody(body)
                , httpHeaders
                , HttpMethod.POST
                , URI.create(uri)
        );
    }

    public static RequestEntity<String> buildGetRequest(String uri) {
        return new RequestEntity<>(
                DEFAULT_GET_HEADERS
                , HttpMethod.GET
                , URI.create(uri)
        );
    }

    private static String getMappedBody(Object body)
            throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(body);
    }

    public HttpHeaders headersWithAuthorization(Long memberId) {
        var headers = buildDefaultHeaders();
        headers.add("Authorization", tokenUtility.issueAccessTokenOnTest(memberId));

        return headers;
    }

    public static HttpHeaders buildPostHeaders(String k, String v) {
        final var headers = buildDefaultPostHeaders();
        headers.add(k, v);
        return headers;
    }

    public static HttpHeaders buildPostHeadersWithAuth(String bearerToken) {
        final var headers = buildDefaultPostHeaders();
        headers.add("Authorization", bearerToken);
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
