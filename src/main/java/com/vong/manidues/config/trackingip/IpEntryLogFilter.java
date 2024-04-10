package com.vong.manidues.config.trackingip;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class IpEntryLogFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String requestProtocol = request.getProtocol();
        String requestIp = request.getRemoteAddr();
        String requestURI = request.getRequestURI();
        String requestMethod = request.getMethod();

        if (requestURI.startsWith("/css/")
                || requestURI.startsWith("/js/")
                || requestURI.startsWith("/img/")
                || requestURI.equals("/favicon.ico")
        ) {
            filterChain.doFilter(request, response);
            return;
        }

        RequestTracker.trackRequest(request);
        log.info("""
                        {} {} {} {}"""
                , requestIp
                , requestProtocol
                , requestMethod
                , requestURI
        );

        if (RequestTracker.getRequestCount(requestIp) > 30) {
            log.info("""
                                                           
                                More than 30 requests in an hour.
                            {}"""
                    , RequestTracker.requestMap.get(requestIp)
            );
        }
        RequestTracker.clearExpiredRequests();
        filterChain.doFilter(request, response);
    }
}
