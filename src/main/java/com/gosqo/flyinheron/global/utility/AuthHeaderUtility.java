package com.gosqo.flyinheron.global.utility;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;

import static com.gosqo.flyinheron.controller.AuthenticationController.REFRESH_TOKEN_COOKIE_NAME;

public class AuthHeaderUtility {
    public static final String[] REFRESH_TOKEN_REQUIRED_URI = {
            "/api/v1/auth/refresh-token"
            , "/api/v1/auth/logout"
    };

    public static boolean needRefreshToken(HttpServletRequest request) {
        return Arrays.stream(REFRESH_TOKEN_REQUIRED_URI).anyMatch(uri -> request.getRequestURI().equals(uri));
    }

    public static boolean isNotAuthenticated(HttpServletRequest request) {

        if (needRefreshToken(request)) {
            String refreshToken = getRefreshToken(request);

            return refreshToken.isBlank();
        }

        String authHeader = request.getHeader("Authorization");

        return authHeader == null || !authHeader.startsWith("Bearer ");
    }

    public static String extractJwt(HttpServletRequest request) throws NullPointerException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || authHeader.equals("null")) {
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
