package com.vong.manidues.global.filter;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RequestLogUtility {
    static final String HOST_DOMAIN = "flyin-heron.duckdns.org";
    static final String HOST_NOT_DOMAIN_MESSAGE = "Requested host is not equal to domain";
    static final String X_REAL_IP = "X-Real-IP";
    static final String X_FORWARDED_FOR = "X-Forwarded-For";
    static final String X_FORWARDED_PROTO = "X-Forwarded-Proto";

    static final String ipFormat = "%-15s";
    static final String methodFormat = "%-7s";
    static final String uriFormat = "%-36s";
    static final String protocolFormat = "%-6s";
    static final String connectionFormat = "%-12s";

    static void logRequestInfo(HttpServletRequest request) {
        final String requestLog = getRequestLog(request);
        log.info(requestLog);
    }

    static void logWholeRequestHeaders(HttpServletRequest request) {
        StringBuffer headers = new StringBuffer();

        request.getHeaderNames().asIterator().forEachRemaining(
                item -> headers
                        .append("\n")
                        .append(item)
                        .append(": ")
                        .append(request.getHeader(item))
        );

        log.warn("{} {}"
                , HOST_NOT_DOMAIN_MESSAGE
                , headers
        );
    }

    static String getRequestLog(HttpServletRequest request) {
        final String formattedIp = String.format(ipFormat, request.getHeader(X_REAL_IP));
        final String formattedMethod = String.format(methodFormat, request.getMethod());
        final String formattedUri = String.format(uriFormat, request.getRequestURI());
        final String formattedProtocol = String.format(protocolFormat, request.getHeader(X_FORWARDED_PROTO));
        final String formattedConnection = String.format(connectionFormat, request.getHeader(HttpHeaders.CONNECTION));
        final String xForwardedFor = request.getHeader(X_FORWARDED_FOR);

        return String.format("%s \"%s %s %s\" %s %s %s"
                , formattedIp
                , formattedMethod
                , formattedUri
                , formattedProtocol
                , formattedConnection
                , request.getHeader(HttpHeaders.USER_AGENT)
                , xForwardedFor
        );
    }
}
