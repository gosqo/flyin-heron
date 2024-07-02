package com.vong.manidues.utility;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthHeaderUtility {

    public static boolean isNotAuthenticated(HttpServletRequest request) {
        String authHeader = extractAuthHeader(request);
        return authHeader == null || !authHeader.startsWith("Bearer ");
    }

    private static String extractAuthHeader(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    public static String extractJwt(HttpServletRequest request) throws NullPointerException {
        String authHeader = extractAuthHeader(request);

        if (authHeader != null && !authHeader.isEmpty())
            return authHeader.substring(7);

        throw new NullPointerException("Can not extract JWT from null");
    }
}
