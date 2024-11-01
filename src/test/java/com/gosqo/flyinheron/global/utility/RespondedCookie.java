package com.gosqo.flyinheron.global.utility;

import org.springframework.http.HttpHeaders;

import java.util.List;
import java.util.Objects;

/**
 * server 응답으로 받은 Set-Cookie 헤더 조회 관련 유틸 메서드 모음
 */
public class RespondedCookie {

    public static String extractTargetSetCookieHeader(List<String> setCookieHeaders, String targetSetCookieName) {
        return setCookieHeaders.stream()
                .filter(item -> {
                    final String[] split = item.split("=");
                    return split[0].equals(targetSetCookieName);
                })
                .findFirst().orElse(null);
    }

    public static String getCookieValue(String targetSetCookieHeader) {
        String[] attributes = targetSetCookieHeader.split("; ");
        return attributes[0].split("=")[1];
    }

    public static String getCookieValue(HttpHeaders headers, String targetSetCookieName) {
        List<String> setCookieHeaders = Objects.requireNonNull(headers.get(HttpHeaders.SET_COOKIE));
        String targetCookieHeader = extractTargetSetCookieHeader(setCookieHeaders, targetSetCookieName);

        return getCookieValue(targetCookieHeader);
    }
}
