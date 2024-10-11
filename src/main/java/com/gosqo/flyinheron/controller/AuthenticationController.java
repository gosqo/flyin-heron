package com.gosqo.flyinheron.controller;

import com.gosqo.flyinheron.dto.auth.AuthenticationRequest;
import com.gosqo.flyinheron.dto.auth.AuthenticationResponse;
import com.gosqo.flyinheron.global.utility.AuthHeaderUtility;
import com.gosqo.flyinheron.service.AuthenticationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    public static final String REFRESH_TOKEN_COOKIE_NAME = "sref";

    private final AuthenticationService service;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> login(
            @Valid @RequestBody AuthenticationRequest request
            , HttpServletResponse response
    ) {
        Map<String, String> tokens = service.authenticate(request);

        addRefreshTokenCookie(tokens.get("refreshToken"), response);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(AuthenticationResponse.builder()
                        .accessToken(tokens.get("accessToken"))
                        .build()
                );
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshToken(
            HttpServletRequest request
            , HttpServletResponse response
    ) {
        final String formerRefreshToken = AuthHeaderUtility.extractRefreshToken(request);

        if (formerRefreshToken == null) {
            throw new IllegalArgumentException("refresh token cannot be null or empty.");
        }

        final Map<String, String> tokens = service.refreshToken(formerRefreshToken);
        final String refreshTokenToResponse = tokens.get("refreshToken");

        if (!formerRefreshToken.equals(refreshTokenToResponse)) {
            addRefreshTokenCookie(refreshTokenToResponse, response);
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(AuthenticationResponse.builder()
                        .accessToken(tokens.get("accessToken"))
                        .build()
                );
    }

    private void addRefreshTokenCookie(String tokens, HttpServletResponse response) {
        Cookie refreshToken = new Cookie(REFRESH_TOKEN_COOKIE_NAME, tokens);

        refreshToken.setAttribute("SameSite", "Strict");
        refreshToken.setMaxAge((int) (refreshTokenExpiration / 1000)); // millis to seconds
        refreshToken.setPath("/");
        refreshToken.setHttpOnly(Boolean.TRUE);

        response.addCookie(refreshToken);
    }
}
