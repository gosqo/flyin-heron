package com.vong.manidues.global.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * WAS HTTP request 수집, 디버깅할 때, info 레벨로 구분
 */
@Component
public class HttpAccessLogFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        if (!request.getHeader("host").equals(RequestLogUtility.HOST_DOMAIN)) {
            RequestLogUtility.logWholeRequestHeaders(request);
        }

        RequestLogUtility.logRequestInfo(request);

        filterChain.doFilter(request, response);
    }
}
