package com.vong.manidues.filter;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FilterUtility {

    final String[] RESOURCES_PERMITTED_TO_ALL_STARTS_WITH = {
            "/css/"
            , "/js/"
            , "/img/"
    };

    boolean startsWithOneOf(String requestURI, String[] array) {
        for (String uri : array) {
            if (requestURI.startsWith(uri)) return true;
        }
        return false;
    }

    void logRequestInfo(HttpServletRequest request) {
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
