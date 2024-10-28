package com.gosqo.flyinheron.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class ResponseBodyWriter {

    public void setResponseWithBody(
            HttpServletResponse response,
            int statusCode,
            String message
    ) {
        response.setStatus(statusCode);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        try {
            response.getWriter().println(
                    new ObjectMapper().writeValueAsString(
                            JsonResponse.builder()
                                    .status(statusCode)
                                    .message(message)
                                    .build()
                    )
            );
        } catch (IOException e) {
            log.error("IOException while writing response.", e);
            response.setStatus(503);
        }
    }
}
