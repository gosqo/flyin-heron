package com.vong.manidues.global.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
@RequiredArgsConstructor
@Slf4j
public class HttpUtility {

    // request to Application (codes til 'request to Nginx' comments show)
    // build headers
    public static HttpHeaders buildDefaultGetHeaders() {
        return new HttpHeaders();
    }

    public static HttpHeaders buildDefaultPostHeaders() {
        var headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        return headers;
    }

    // when Request Entity with HTTP "GET" method needed
    public static RequestEntity<String> buildGetRequestEntity(String uri) {
        return new RequestEntity<>(
                buildDefaultGetHeaders()
                , HttpMethod.GET
                , URI.create(uri)
        );
    }

    public static RequestEntity<String> buildGetRequestEntity(HttpHeaders httpHeaders, String uri) {
        return new RequestEntity<>(
                httpHeaders
                , HttpMethod.GET
                , URI.create(uri)
        );
    }

    // when Request Entity with HTTP "POST" method needed
    public static RequestEntity<String> buildPostRequestEntity(Object body, String uri)
            throws JsonProcessingException {
        return new RequestEntity<>(
                getMappedBody(body)
                , buildDefaultPostHeaders()
                , HttpMethod.POST
                , URI.create(uri)
        );
    }

    public static RequestEntity<String> buildPostRequestEntity(HttpHeaders httpHeaders, Object body, String uri)
            throws JsonProcessingException {
        return new RequestEntity<>(
                getMappedBody(body)
                , httpHeaders
                , HttpMethod.POST
                , URI.create(uri)
        );
    }

    // DTO into json stringified.
    private static String getMappedBody(Object body)
            throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(body);
    }

    public static HttpHeaders buildPostHeaders(String k, String v) {
        final var headers = buildDefaultPostHeaders();
        headers.add(k, v);
        return headers;
    }

    public static HttpHeaders buildPostHeadersWithBearerToken(String bearerToken) {
        return buildPostHeaders("Authorization", bearerToken);
    }

    public static HttpHeaders buildPostHeadersWithToken(String token) {
        return buildPostHeaders("Authorization", "Bearer " + token);
    }

    public static <T> void logResponse(ResponseEntity<T> response) {
        log.info(response.getBody() == null
                        ? "response: {}\nheaders: {}"
                        : "response: {}\nheaders: {}\nbody: {}"
                , response.getStatusCode()
                , response.getHeaders()
                , response.getBody()
        );
    }

    // when request to Nginx Server, HTTP "GET"
    public static HttpHeaders buildNginxGetHeaders() {
        var headers = new HttpHeaders();
        headers.add("Connection", "Keep-Alive");
        headers.add("User-Agent", "Mozilla");
        return headers;
    }

    public static RequestEntity<String> buildNginxGetRequestEntity(String uri) {
        return new RequestEntity<>(
                buildNginxGetHeaders()
                , HttpMethod.GET
                , URI.create(uri)
        );
    }
}
