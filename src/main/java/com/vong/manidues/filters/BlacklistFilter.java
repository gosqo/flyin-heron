package com.vong.manidues.filters;

import com.vong.manidues.filters.trackingip.BlacklistIp;
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
public class BlacklistFilter extends OncePerRequestFilter {

    private final FilterUtility filterUtility;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String requestIp = request.getRemoteAddr();
        String requestURI = request.getRequestURI();

        if (filterUtility.startsWithOneOf(
                requestURI, filterUtility.RESOURCES_PERMITTED_TO_ALL_STARTS_WITH)
                || requestURI.equals("/favicon.ico")
        ) {
            filterChain.doFilter(request, response);
            return;
        }

        if (BlacklistIp.blacklistedIps.contains(requestIp)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);

            log.info("*** Request from blacklisted IP *** response with {}"
                    , response.getStatus());

            return;
        }

        if (RequestTracker.getRequestCount(requestIp) > 70) {
            BlacklistIp.blacklistedIps.add(requestIp);

            response.setStatus(HttpServletResponse.SC_FORBIDDEN);

            log.info("*** Repetitional request over 70 requests within 10 seconds.***");
            log.info("""
                            Size of blacklist:{}
                                {}"""
                    , BlacklistIp.blacklistedIps.size()
                    , BlacklistIp.getBlacklistedIps()
            );
            log.info("\n{}", RequestTracker.getWholeRequestMap());

            return;
        }

        filterChain.doFilter(request, response);
    }
}
