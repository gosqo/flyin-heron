package com.vong.manidues.global.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

/**
 * WAS HTTP request 수집, 디버깅할 때, info 레벨로 구분
 */
@Component
@RequiredArgsConstructor
public class HttpAccessLogFilter extends OncePerRequestFilter {
    private final Environment environment;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String[] profiles = environment.getActiveProfiles();
        boolean isWin = Arrays.asList(profiles).contains("win");

        if (isWin) {
            if (request.getHeader("host") == null
                    || !request.getHeader("host").equals(RequestLogUtility.HOST_DOMAIN)
            ) {
            RequestLogUtility.logWholeRequestHeaders(request);
            }
        }


        RequestLogUtility.logRequestInfo(request);

        filterChain.doFilter(request, response);
    }
}
