package com.vong.manidues.filter;

import com.vong.manidues.config.SecurityConfig;
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

    private static boolean isUnregisteredURI(String requestURI, String[] registeredURIs) {
        for (String registeredURI : registeredURIs) {
            if (registeredURI.equals("/")) continue;
            if (registeredURI.endsWith("/**")) {
                registeredURI = registeredURI.substring(
                        0, registeredURI.lastIndexOf("/") + 1
                );
            }

            if (registeredURI.endsWith("/")) {
                if (requestURI.startsWith(registeredURI)) return false;
            } else {
                if (requestURI.matches(registeredURI)) return false;
            }
        }
        return true;
    }

    private boolean isUnregisteredGetURI(String requestURI) {
        if (requestURI.equals("/")) return false;

        return isUnregisteredURI(requestURI, SecurityConfig.WHITE_LIST_URIS_NON_MEMBER_GET);
    }

    private boolean isUnregisteredPostURI(String requestURI) {
        return isUnregisteredURI(requestURI, SecurityConfig.WHITE_LIST_URIS_NON_MEMBER_POST);
    }

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
        String authHeader = request.getHeader("Authorization");

        if (filterUtility.startsWithOneOf(requestURI,
                filterUtility.RESOURCES_PERMITTED_TO_ALL_STARTS_WITH)
                || requestURI.equals("/favicon.ico")
        ) {
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

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            if (requestMethod.equalsIgnoreCase("get")) {
                if (isUnregisteredGetURI(requestURI)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    log.warn("*** Request to unregistered URI *** response with {}", response.getStatus());

                    return;
                }
            }

            if (requestMethod.equalsIgnoreCase("post")) {
                if (isUnregisteredPostURI(requestURI)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    log.warn("*** Request to unregistered URI *** response with {}", response.getStatus());

                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
