package com.vong.manidues.filters;

import com.vong.manidues.filters.trackingip.RequestTracker;
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
@Slf4j
@RequiredArgsConstructor
public class IpEntryLogFilter extends OncePerRequestFilter {

    private final FilterUtility filterUtility;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        if (filterUtility.startsWithOneOf(requestURI,
                filterUtility.RESOURCES_PERMITTED_TO_ALL_STARTS_WITH)
                || requestURI.equals("/favicon.ico")
        ) {
            filterChain.doFilter(request, response);
            return;
        }

        filterUtility.logRequestInfo(request);
        RequestTracker.trackRequest(request);
        RequestTracker.clearExpiredRequests();
        filterChain.doFilter(request, response);
    }
}
