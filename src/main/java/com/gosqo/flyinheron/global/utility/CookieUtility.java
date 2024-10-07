package com.gosqo.flyinheron.global.utility;

import jakarta.servlet.http.Cookie;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;

@Component
public class CookieUtility {

    /**
     * cookie value 의 길이를 앞에서 부터 index 500 이후에 위치한 구분자까지 자름.
     *
     * @param cookie    value 를 자를 쿠키.
     * @param delimiter cookie value 의 구분자
     */
    public static void trimFront500Bytes(Cookie cookie, Character delimiter) {
        final String cookieValue = cookie.getValue();
        final String trimmedCookieValue = cookieValue.substring(
                cookieValue.indexOf(delimiter, 500) + 1
        );

        cookie.setValue(trimmedCookieValue);
    }

    /**
     * 쿠키 배열 중, 특정 이름의 쿠키의 존재 여부 확인.
     *
     * @param cookieName 존재 여부를 확인할 쿠키의 이름.
     * @param cookies    쿠키 배열.
     * @return cookieName 을 이름으로 가진 쿠키가 배열에 존재하면 true, 아니면 false.
     */
    public static boolean hasCookieNamed(String cookieName, Cookie[] cookies) {
        if (cookies == null) return false;

        return Arrays.stream(cookies).anyMatch(cookie -> cookie.getName().equals(cookieName));
    }

    public static boolean over3500BytesOf(Cookie cookie) {
        return getCookieValueSize(cookie) > 3500;
    }

    /**
     * @param cookie value 의 byte 크기를 확인할 쿠키.
     * @return cookie value 의 byte 크기.
     */
    public static int getCookieValueSize(Cookie cookie) {
        return (cookie.getValue().getBytes()).length;
    }

    /**
     * 주어진 value 를 delimiter 와 함께 cookie value 에 덧붙임.
     *
     * @param value     덧붙이고자 하는 값.
     * @param cookie    값을 덧붙일 쿠키.
     * @param delimiter 기존의 값과 구분하기 위한 구분자.
     */
    public static void appendValue(
            Object value
            , Cookie cookie
            , Character delimiter
    ) {
        String updatedValue = cookie.getValue()
                + delimiter + ObjectUtils.nullSafeToString(value);

        cookie.setValue(updatedValue);
    }

    /**
     * 쿠키 배열 중, 특정 이름의 쿠키를 반환.
     *
     * @param cookieName 반환 받고자하는 쿠키의 이름.
     * @param cookies    쿠키 배열.
     * @return cookieName 을 이름으로 가진 쿠키, 배열에 존재하지 않는다면 null.
     */
    public static Cookie findCookie(String cookieName, Cookie[] cookies) {
        if (cookies == null) {
            return null;
        }

        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(cookieName))
                .findFirst()
                .orElse(null);
    }

    /**
     * 주어진 cookie 객체에 특정 value 의 존재 여부 확인.
     *
     * @param target 존재 여부를 확인할 value.
     * @param cookie 대상 쿠키
     * @return 대상 쿠키의 value 에 찾고자하는 value 가 있다면 true, 아니면 false.
     */
    public static boolean contains(Object target, Cookie cookie) {
        String[] splitValues = splitCookieValue('/', cookie);
        return Arrays.stream(splitValues).anyMatch(value -> value.equals(String.valueOf(target)));
    }

    /**
     * 매개변수 delimiter 를 사용해, Cookie 객체의 value 를 구분.
     *
     * @param delimiter value 를 나누는 기준 문자.
     * @param cookie    나누고자하는 value 를 가진 cookie
     * @return delimiter 로 구분된 value 배열.
     */
    public static String[] splitCookieValue(@NotNull Character delimiter, Cookie cookie) {
        return cookie.getValue().split(String.valueOf(delimiter));
    }
}
