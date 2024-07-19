package com.vong.manidues.global.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ResponseBodyWriter {

    public void setResponseWithBody(
            HttpServletResponse response,
            int statusCode,
            String message
    ) throws IOException {
        response.setStatus(statusCode);
        response.setContentType(MediaType.APPLICATION_JSON.toString());
        response.setCharacterEncoding("UTF-8");
        response.getWriter().println(writeBody(statusCode, message));
    }

    private String writeBody(int statusCode, String message)
            throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(
                buildResponseBody(statusCode, message)
        );
    }

    private JsonResponse buildResponseBody(int statusCode, String message) {
        return JsonResponse.builder()
                .status(statusCode)
                .message(message)
                .build();
    }
}
