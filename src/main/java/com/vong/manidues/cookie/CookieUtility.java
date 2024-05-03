package com.vong.manidues.cookie;

import jakarta.servlet.http.Cookie;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class CookieUtility {

    /**
     * cookie value 의 길이를 앞에서 부터 index 500 이후에 위치한 구분자까지 자름.
     * @param cookie value 를 자를 쿠키.
     * @param delimiter cookie value 의 구분자
     */
    public void cutFirst500BytesWith(Cookie cookie, Character delimiter) {
        String cookieValue = cookie.getValue();
        String cutCookieValue = cookieValue.substring(
                cookieValue.indexOf(delimiter, 500) + 1
        );

        cookie.setValue(cutCookieValue);
    }

    /**
     *
     * @param cookie value 의 byte 크기를 확인할 쿠키.
     * @return cookie value 의 byte 크기.
     */
    public int getCookieValueSize(Cookie cookie) {
        return (cookie.getValue().getBytes()).length;
    }

    /**
     * 주어진 value 를 delimiter 와 함께 cookie value 에 덧붙임.
     * @param value 덧붙이고자 하는 값.
     * @param cookie 값을 덧붙일 쿠키.
     * @param delimiter 기존의 값과 구분하기 위한 구분자.
     */
    public void appendCookieValueWith(
            Object value
            , Cookie cookie
            , Character delimiter
    ) {
        String updatedValue = cookie.getValue()
                + delimiter + ObjectUtils.nullSafeToString(value);
        
        cookie.setValue(updatedValue);
    }

    /**
     * 주어진 cookie 객체에 특정 value 의 존재 여부 확인.
     * @param value 존재 여부를 확인할 value.
     * @param cookie 대상 쿠키
     * @return 대상 쿠키의 value 에 찾고자하는 value 가 있다면 true, 아니면 false.
     */
    public boolean hasSpecificValueIn(Object value, Cookie cookie) {
        String[] splitValues = splitCookieValueWith(cookie, '/');

        for (String splitValue : splitValues) {
            if (splitValue.equals(ObjectUtils.nullSafeToString(value))) return true;
        }
        return false;
    }

    /**
     * 매개변수 delimiter 를 사용해, Cookie 객체의 value 를 구분.
     * @param cookie 나누고자하는 value 를 가진 cookie
     * @param delimiter value 를 나누는 기준 문자.
     * @return delimiter 로 구분된 value 배열.
     */
    public String[] splitCookieValueWith(Cookie cookie, Character delimiter) {
        return cookie.getValue().split(ObjectUtils.nullSafeToString(delimiter));
    }

    /**
     * 쿠키 배열 중, 특정 이름의 쿠키의 존재 여부 확인.
     * @param cookieName 존재 여부를 확인할 쿠키의 이름.
     * @param cookies 쿠키 배열.
     * @return cookieName 을 이름으로 가진 쿠키가 배열에 존재하면 true, 아니면 false.
     */
    public boolean hasCookieNamed(String cookieName, Cookie[] cookies) {
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieName)) return true;
        }
        return false;
    }

    /**
     * 쿠키 배열 중, 특정 이름의 쿠키를 반환. 
     * @param cookieName 반환 받고자하는 쿠키의 이름.
     * @param cookies 쿠키 배열.
     * @return cookieName 을 이름으로 가진 쿠키, 배열에 존재하지 않는다면 null. 
     */
    public Cookie getCookie(String cookieName, Cookie[] cookies) {
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieName)) {
                return cookie;
            }
        }
        return null;
    }
}
