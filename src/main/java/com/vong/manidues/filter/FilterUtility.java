package com.vong.manidues.filter;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FilterUtility {

    private static final String[] STATIC_RESOURCES_PERMITTED_TO_ALL = {
            "/css/"
            , "/js/"
            , "/img/"
    };

    static boolean isStaticUri(String requestURI) {
        return startsWithOneOf(requestURI, STATIC_RESOURCES_PERMITTED_TO_ALL)
                || requestURI.equals("/favicon.ico");
    }

    private static boolean startsWithOneOf(String requestURI, String[] array) {
        for (String uri : array) {
            if (requestURI.startsWith(uri)) return true;
        }
        return false;
    }

    public static void logRequestInfo(HttpServletRequest request) {
        final String ipFormat = String.format("%-15s", request.getRemoteAddr());
        final String method = String.format("%-7s", request.getMethod());
        final String uri = String.format("%-30s", request.getRequestURI());
        final String protocol = String.format("%-8s", request.getProtocol());
        final String connection = String.format("%-12s", request.getHeader("Connection"));

        log.info("""
                        {} "{} {} {}" {} {}"""
                , ipFormat
                , method
                , uri
                , protocol
                , connection
                , request.getHeader("User-Agent")
        );
    }
}
