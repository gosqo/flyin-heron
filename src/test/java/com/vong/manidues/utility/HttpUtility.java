package com.vong.manidues.utility;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

public class HttpUtility {

    public static final HttpHeaders DEFAULT_HTTP_HEADERS = new HttpHeaders();

    static {
        DEFAULT_HTTP_HEADERS.add("Connection", "Keep-Alive");
        DEFAULT_HTTP_HEADERS.add("User-Agent", "Mozilla");
    }

    public static final HttpEntity<String> DEFAULT_HTTP_ENTITY = new HttpEntity<>(HttpUtility.DEFAULT_HTTP_HEADERS);
}
