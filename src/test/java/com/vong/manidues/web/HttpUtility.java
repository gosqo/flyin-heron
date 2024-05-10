package com.vong.manidues.web;

import com.vong.manidues.token.TokenUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HttpUtility {

    private final TokenUtility tokenUtility;

    public static final HttpHeaders DEFAULT_HTTP_HEADERS = new HttpHeaders();

    public static final HttpEntity<String> DEFAULT_HTTP_ENTITY = new HttpEntity<>(HttpUtility.DEFAULT_HTTP_HEADERS);

    static {
        DEFAULT_HTTP_HEADERS.add("Connection", "Keep-Alive");
        DEFAULT_HTTP_HEADERS.add("User-Agent", "Mozilla");
    }

    public HttpHeaders headersWithAuthorization(Long memberId) {
        HttpHeaders headers = new HttpHeaders(DEFAULT_HTTP_HEADERS);

        headers.add(
                "Authorization"
                , tokenUtility.issueAccessTokenOnTest(memberId)
        );

        return headers;
    }

}
