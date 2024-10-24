package com.gosqo.flyinheron.global.utility;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class HeadersUtility {

    public static HttpHeaders buildHeadersContentTypeJson() {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    public static HttpHeaders buildMultipartHeaders() {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        return headers;
    }

    public static HttpHeaders buildHeadersWithToken(HttpHeaders headers, String token) {
        headers.add("Authorization", "Bearer " + token);

        return headers;
    }

    public static HttpHeaders buildHeadersWithToken(String token) {
        HttpHeaders headers = new HttpHeaders();

        headers.add("Authorization", "Bearer " + token);

        return headers;
    }

    // when request to Nginx Server, HTTP "GET"
    public static HttpHeaders buildNginxGetHeaders() {
        var headers = new HttpHeaders();
        headers.add("Connection", "Keep-Alive");
        headers.add("User-Agent", "Mozilla chrome");
        return headers;
    }
}
