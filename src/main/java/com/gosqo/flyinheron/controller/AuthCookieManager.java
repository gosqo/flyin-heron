package com.gosqo.flyinheron.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AuthCookieManager {
    public static final String REFRESH_TOKEN_COOKIE_NAME = "sref";

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    void addRefreshTokenCookie(String tokens, HttpServletResponse response) {
        Cookie refreshToken = new Cookie(REFRESH_TOKEN_COOKIE_NAME, tokens);

        refreshToken.setAttribute("SameSite", "Strict");
        refreshToken.setMaxAge((int) (refreshTokenExpiration / 1000)); // millis to seconds
        refreshToken.setPath("/");
        refreshToken.setHttpOnly(Boolean.TRUE);

        response.addCookie(refreshToken);
    }
}
