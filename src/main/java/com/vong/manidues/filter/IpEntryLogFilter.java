package com.vong.manidues.filter;

import com.vong.manidues.filter.trackingip.RequestTracker;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.vong.manidues.filter.FilterUtility.isStaticUri;
import static com.vong.manidues.filter.FilterUtility.logRequestInfo;

@Component
@Slf4j
public class IpEntryLogFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        if (isStaticUri(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        logRequestInfo(request);
        RequestTracker.trackRequest(request);
        RequestTracker.clearExpiredRequests();
        filterChain.doFilter(request, response);
    }
}
