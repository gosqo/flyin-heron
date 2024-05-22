package com.vong.manidues.filter;

import com.vong.manidues.config.SecurityConfig;
import com.vong.manidues.utility.AuthHeaderUtility;
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
@RequiredArgsConstructor
@Slf4j
public class AbnormalRequestFilter extends OncePerRequestFilter {

    private final FilterUtility filterUtility;
    private final AuthHeaderUtility authUtility;
    private final String[] WHITE_LIST_USER_AGENTS = {
            "mozilla"
            , "postman"
    };

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        String requestMethod = request.getMethod();
        String userAgent = request.getHeader("User-Agent");
        String connection = request.getHeader("Connection");

        if (filterUtility.isUriPermittedToAll(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (isAbnormalUserAgent(userAgent)
                || isAbnormalConnection(connection)
        ) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            log.warn("*** Request from not allowed UA or Connection *** response with {}", response.getStatus());

            return;
        }

        if (authUtility.isNotAuthenticated(request)) {
            if (requestMethod.equalsIgnoreCase("get")
                    && isNotPermittedToAllGetURI(requestURI)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                log.warn("*** Request to unregistered URI *** response with {}", response.getStatus());

                return;
            }

            if (requestMethod.equalsIgnoreCase("post")
                    && isNotPermittedToAllPostURI(requestURI)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                log.warn("*** Request to unregistered URI *** response with {}", response.getStatus());

                return;
            }
        }
        filterChain.doFilter(request, response);
    }

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

    private static boolean isNotPermittedToAllURI(String requestURI, String[] registeredURIs) {
        for (String registeredURI : registeredURIs) {
            if (requestURI.equals(registeredURI)) return false;

            if (registeredURI.endsWith("/**")) {
                String trimmedURI = registeredURI.substring(
                        0, registeredURI.lastIndexOf("/") + 1
                );
                if (requestURI.startsWith(trimmedURI)) return false;
            }
        }
        return true;
    }

    private boolean isNotPermittedToAllGetURI(String requestURI) {
        return isNotPermittedToAllURI(requestURI, SecurityConfig.WHITE_LIST_URIS_NON_MEMBER_GET);
    }

    private boolean isNotPermittedToAllPostURI(String requestURI) {
        return isNotPermittedToAllURI(requestURI, SecurityConfig.WHITE_LIST_URIS_NON_MEMBER_POST);
    }
}
