package com.vong.manidues.config.trackingip;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BlacklistedIpsFilter extends OncePerRequestFilter {

    @Value("${blacklisted-ips-list}")
    private List<String> blacklistedIps;
    private final String[] WHITE_LIST_USER_AGENTS = {
            "mozilla"
            , "postman"
    };

    private boolean isWhiteListUserAgent(String userAgent) {
        for (String whiteUserAgent : WHITE_LIST_USER_AGENTS) {
            if (userAgent.toLowerCase().contains(whiteUserAgent)) return true;
        }
        return false;
    }

    private boolean isAbnormalUserAgent(String userAgent) {
        return userAgent == null
                || userAgent.isBlank()
                || !isWhiteListUserAgent(userAgent);
    }

    private boolean isAbnormalConnection(String connection) {
        return connection == null
                || !connection.equalsIgnoreCase("keep-alive");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String requestIp = request.getRemoteAddr();
        String requestUserAgent = request.getHeader("User-Agent");
        String requestConnection = request.getHeader("Connection");

        if (isAbnormalUserAgent(requestUserAgent)
                || isAbnormalConnection(requestConnection)
        ) {
            log.warn("""

                                *** Abnormal request ***
                                IP: {}
                                User-Agent: {}
                                Connection: {}
                            """
                    , requestIp
                    , requestUserAgent
                    , requestConnection
            );

            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        if (blacklistedIps.contains(requestIp)) {
            log.warn("""

                                *** Request from blacklisted ip ***
                                IP: {}
                                User-Agent: {}
                                Connection: {}
                                listed size is: {}
                            """
                    , requestIp
                    , requestUserAgent
                    , requestConnection
                    , blacklistedIps.size()
            );

            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        filterChain.doFilter(request, response);
    }
}
