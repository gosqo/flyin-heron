package com.gosqo.flyinheron.global.filter;

import com.gosqo.flyinheron.global.utility.AuthHeaderUtility;
import com.gosqo.flyinheron.service.ClaimExtractor;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
// 매 요청 마다 필터를 거치도록 extends
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final ClaimExtractor claimExtractor;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String jwt;
        final String userEmail;

        if (AuthHeaderUtility.isNotAuthenticated(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = request.getRequestURI().equals("/api/v1/auth/refresh-token")
                ? AuthHeaderUtility.getRefreshToken(request)
                : AuthHeaderUtility.extractJwt(request);
        userEmail = extractUserEmail(jwt, response);

        if (userEmail == null) {
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }

    private String extractUserEmail(String jwt, HttpServletResponse response) throws IOException {
        String userEmail;
        try {
            userEmail = claimExtractor.extractUserEmail(jwt);

            return userEmail;
        } catch (ExpiredJwtException ex) {
            response.sendError(401, "토큰 만료.");
        } catch (JwtException ex) {
            log.warn("""
                            *** manipulated token *** response with 400. {}: {}
                            tried token: {}"""
                    , ex.getClass().getName()
                    , ex.getMessage()
                    , jwt
            );
            response.sendError(400, "올바른 접근이 아닙니다. 로그아웃 후 다시 로그인 해주십시오.");
        } catch (Exception ex) {
            log.warn(ex.getMessage());
            response.sendError(500, "인증 정보에 문제가 있습니다. 로그아웃 후 로그인 하심시오.");
        }

        return null;
    }
}
