package com.vong.manidues.utility;

import com.vong.manidues.token.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthHeaderUtility {

    private final JwtService jwtService;

    public boolean isNotAuthenticated(HttpServletRequest request) {
        String authHeader = extractAuthHeader(request);
        return authHeader == null || !authHeader.startsWith("Bearer ");
    }

    public String extractAuthHeader(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    public String extractJwtFromHeader(HttpServletRequest request) throws NullPointerException {
        String authHeader = extractAuthHeader(request);

        if (authHeader != null && !authHeader.isEmpty())
            return authHeader.substring(7);

        throw new NullPointerException("Can not extract JWT from null");
    }

    public String extractEmailFromHeader(HttpServletRequest request) {
        return jwtService.extractUserEmail(extractJwtFromHeader(request));
    }
}
