package com.vong.manidues.utility;

import com.vong.manidues.token.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthHeaderUtility {

    private final JwtService jwtService;

    public boolean isNotAuthenticated(String authHeader) {
        return authHeader == null || !authHeader.startsWith("Bearer ");
    }

    public String extractAuthHeader(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    public String extractJwtFromHeader(HttpServletRequest request) {
        return extractAuthHeader(request).substring(7);
    }

    public String extractEmailFromHeader(HttpServletRequest request) {
        return jwtService.extractUserEmail(extractJwtFromHeader(request));
    }
}
