package com.vong.manidues.utility;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class HttpResponseWithBody {

    public void jsonResponse(
            HttpServletResponse response,
            int statusCode,
            String message,
            @Nullable String additionalMessage
    ) throws IOException {
        response.setStatus(statusCode);
        response.setContentType(MediaType.APPLICATION_JSON.toString());
        response.setCharacterEncoding("UTF-8");
        response.getWriter().println(new CustomJsonMapper().mapToJsonString(
                JsonResponseBody.builder()
                        .message(message)
                        .additionalMessage(additionalMessage)
                        .build()
        ));
    }
}
