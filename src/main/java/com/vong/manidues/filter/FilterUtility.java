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
        String ipFormat = String.format("%-15s", request.getRemoteAddr());

        log.info("""
                        {} "{} {} {}" {} {}"""
                , ipFormat
                , request.getMethod()
                , request.getRequestURI()
                , request.getProtocol()
                , request.getHeader("User-Agent")
                , request.getHeader("Connection")
        );
    }
}
