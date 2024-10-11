package com.gosqo.flyinheron.global.utility;

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
            final String refreshTokenCookieValue = CookieUtility
                    .findCookie(REFRESH_TOKEN_COOKIE_NAME, request.getCookies())
                    .getValue();

            return refreshTokenCookieValue == null || refreshTokenCookieValue.isBlank();
        }

        String authHeaderValue = request.getHeader("Authorization");

        return authHeaderValue == null || !authHeaderValue.startsWith("Bearer ");
    }

    public static String extractAccessToken(HttpServletRequest request) throws NullPointerException {
        String authHeaderValue = request.getHeader("Authorization");

        if (authHeaderValue == null || authHeaderValue.equals("null")) {
            return null;
        }

        return authHeaderValue.substring(7); // accessToken
    }

    public static String extractRefreshToken(HttpServletRequest request) {
        final String refreshTokenCookieValue = CookieUtility
                .findCookie(REFRESH_TOKEN_COOKIE_NAME, request.getCookies())
                .getValue();

        if (refreshTokenCookieValue == null || refreshTokenCookieValue.isBlank()) {
            return null;
        }

        return refreshTokenCookieValue; // refreshToken
    }
}
