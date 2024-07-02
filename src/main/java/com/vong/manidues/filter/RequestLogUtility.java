package com.vong.manidues.filter;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RequestLogUtility {
    static final String ipFormat = "%-15s";
    static final String methodFormat = "%-7s";
    static final String uriFormat = "%-36s";
    static final String protocolFormat = "%-8s";
    static final String connectionFormat = "%-12s";

    static void logRequestInfo(HttpServletRequest request) {
        final String requestLog = getRequestLog(request);
        log.info(requestLog);
    }

    static String getRequestLog(HttpServletRequest request) {
        final String formattedIp = String.format(ipFormat, request.getRemoteAddr());
        final String formattedMethod = String.format(methodFormat, request.getMethod());
        final String formattedUri = String.format(uriFormat, request.getRequestURI());
        final String formattedProtocol = String.format(protocolFormat, request.getProtocol());
        final String formattedConnection = String.format(connectionFormat, request.getHeader("Connection"));

        return String.format("%s \"%s %s %s\" %S %s"
                , formattedIp
                , formattedMethod
                , formattedUri
                , formattedProtocol
                , formattedConnection
                , request.getHeader("User-Agent")
        );
    }
}
