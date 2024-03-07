package com.vong.manidues.utility;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomServletResponse {


    public void jsonResponse(HttpServletResponse response, int statusCode, Object obj) throws IOException {
        CustomJsonMapper jsonMapper = new CustomJsonMapper();

        response.setStatus(statusCode);
        response.setContentType(MediaType.APPLICATION_JSON.toString());
        response.setCharacterEncoding("UTF-8");
        response.getWriter().println(jsonMapper.mapToJsonString(obj));
    }
}
