package com.vong.manidues.filter;

import com.vong.manidues.filter.trackingip.Blacklist;
import com.vong.manidues.filter.trackingip.RequestTracker;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class BlacklistFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String requestIp = request.getRemoteAddr();

        RequestTracker.trackRequest(request);
        RequestTracker.clearExpiredRequests();

        if (Blacklist.blacklistedIps.contains(requestIp)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            log.info("*** blacklisted IP *** response with 404, {}", RequestLogUtility.getRequestLog(request));

            return;
        }

        if (RequestTracker.getRequestCount(requestIp) > 70) {
            Blacklist.blacklistedIps.add(requestIp);

            response.setStatus(HttpServletResponse.SC_NOT_FOUND);

            log.warn("*** 70 requests within 10 seconds.***");
            log.warn("""
                            Size of blacklist:{}
                                {}"""
                    , Blacklist.blacklistedIps.size()
                    , Blacklist.getBlacklistedIps()
            );
            log.info("\n{}", RequestTracker.getWholeRequestMap());

            return;
        }

        filterChain.doFilter(request, response);
    }
}
