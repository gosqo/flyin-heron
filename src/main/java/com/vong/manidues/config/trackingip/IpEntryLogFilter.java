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
        String requestIp = request.getRemoteAddr();
        String requestUserAgent = request.getHeader("User-Agent");
        String requestURI = request.getRequestURI();
        String requestMethod = request.getMethod();
        String requestConnection = request.getHeader("Connection");

        if (requestURI.startsWith("/css/")
         || requestURI.startsWith("/js/")
                || requestURI.startsWith("/img/")
        ) {
            filterChain.doFilter(request, response);
            return;
        }

        RequestTracker.trackRequest(request);
        log.info("""

                            === Request Info ===
                            Ip: {}
                            User-Agent: {}
                            Method: {}
                            URI: {}
                            Connection: {}
                        """
                , requestIp
                , requestUserAgent
                , requestMethod
                , requestURI
                , requestConnection
        );

        if (RequestTracker.getRequestCount(requestIp) > 10) {
            log.info("""
                                                       
                            This client has more than 10 requests in an hour.
                            RequestTracker is like:
                            {}
                        """, RequestTracker.getRequestMap()
            );
        }
        RequestTracker.clearExpiredRequests();

        filterChain.doFilter(request, response);
    }
}
