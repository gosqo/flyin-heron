package com.gosqo.flyinheron.global.utility;

import java.util.List;

/**
 * server 응답으로 받은 Set-Cookie 헤더 조회 관련 유틸 메서드 모음
 */
public class RespondedCookie {

    public static String extract(List<String> setCookieHeaders, String cookieName) {
        return setCookieHeaders.stream()
                .filter(item -> {
                    final String[] split = item.split("=");
                    return split[0].equals(cookieName);
                })
                .findFirst().orElse(null);
    }

    public static String getCookieValue(String cookie) {
        String[] attributes = cookie.split("; ");
        return attributes[0].split("=")[1];
    }
}
