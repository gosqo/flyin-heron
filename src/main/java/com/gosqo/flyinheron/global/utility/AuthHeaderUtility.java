package com.gosqo.flyinheron.global.utility;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import static com.gosqo.flyinheron.controller.AuthenticationController.REFRESH_TOKEN_COOKIE_NAME;

public class AuthHeaderUtility {

    private static String extractAuthHeader(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    public static boolean isNotAuthenticated(HttpServletRequest request) {
        String refreshToken = getRefreshToken(request);
        String authHeader = extractAuthHeader(request);

        return (authHeader == null || !authHeader.startsWith("Bearer ")) && refreshToken.isBlank();
    }

    public static String extractJwt(HttpServletRequest request) throws NullPointerException {
        String authHeader = extractAuthHeader(request);

        if (authHeader == null) {
            return null;
        }

        return authHeader.substring(7);
    }

    public static String getRefreshToken(HttpServletRequest request) {
        String refreshToken = "";

        Cookie cookie = CookieUtility.findCookie(REFRESH_TOKEN_COOKIE_NAME, request.getCookies());

        if (cookie != null) {
            refreshToken = cookie.getValue();
        }

        return refreshToken;
    }
}
