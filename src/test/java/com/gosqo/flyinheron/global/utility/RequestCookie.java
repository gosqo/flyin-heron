package com.gosqo.flyinheron.global.utility;

/**
 * 서버 요청에 담을 쿠키 관련 유틸 메서드
 */
public class RequestCookie {

    public static String valueWith(String key, String value) {
        return String.format("%s=%s;", key, value);
    }
}
