package com.vong.manidues.global.filter;

import com.vong.manidues.global.utility.AuthHeaderUtility;
import com.vong.manidues.service.ClaimExtractor;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtExceptionFilter extends OncePerRequestFilter {

    private final ClaimExtractor claimExtractor;

    @Override
    public void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws IOException, ServletException {
        if (AuthHeaderUtility.isNotAuthenticated(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwToken = AuthHeaderUtility.extractJwt(request);

        if (throwAnyJwtException(response, jwToken)) return;

        filterChain.doFilter(request, response);
    }

    private boolean throwAnyJwtException(
            HttpServletResponse response
            , String jwToken
    ) throws IOException {

        try {
            claimExtractor.extractUserEmail(jwToken);
        } catch (ExpiredJwtException ex) {
            log.debug("expired token, normal user will request to POST /api/v1/auth/refresh-token");
            response.sendError(401, "토큰 만료.");

            return true;
        } catch (JwtException ex) {
            log.warn("""
                            *** manipulated token *** response with 400. {}: {}
                            tried token: {}"""
                    , ex.getClass().getName()
                    , ex.getMessage()
                    , jwToken
            );
            response.sendError(400, "올바른 접근이 아닙니다. 로그아웃 후 다시 로그인 해주십시오.");

            return true;
        }
        return false;
    }
}
