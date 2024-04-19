package com.vong.manidues.filter;

import com.vong.manidues.token.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtExceptionFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    public void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws IOException, ServletException {
        String authHeader = request.getHeader("Authorization");
        String refreshToken;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        refreshToken = authHeader.substring(7);

        try {
            jwtService.extractExpiration(refreshToken);
        } catch (JwtException ex) {
            if (ex instanceof ExpiredJwtException) {
                log.info("expired token, normal user will request to POST /api/v1/auth/refresh-token)");
                response.sendError(401, "토큰 만료.");
            } else {
                log.info("""
                        *** manipulated token *** response with 400. {}
                        authHeader: {}"""
                        , ex.getClass()
                        , authHeader
                );
                response.sendError(400, "조작된 토큰.");
            }
            return;
        }

        filterChain.doFilter(request, response);
    }
}
