package com.vong.manidues.global.utility;

import jakarta.servlet.http.HttpServletRequest;

public class AuthHeaderUtility {

    private static String extractAuthHeader(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    public static boolean isNotAuthenticated(HttpServletRequest request) {
        String authHeader = extractAuthHeader(request);
        return authHeader == null || !authHeader.startsWith("Bearer ");
    }

    public static String extractJwt(HttpServletRequest request) throws NullPointerException {
        String authHeader = extractAuthHeader(request);

        if (isNotAuthenticated(request)) {
            throw new NullPointerException("Can not extract JWT from null");
        }

        return authHeader.substring(7);
    }
}
