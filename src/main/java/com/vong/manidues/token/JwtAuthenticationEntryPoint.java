package com.vong.manidues.token;

import com.vong.manidues.utility.CustomServletResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        CustomServletResponse customServletResponse = new CustomServletResponse();
        customServletResponse.jsonResponse(
                response,
                401,
                JwtExceptionResponse.builder()
                        .exceptionMessage("인증 정보가 필요합니다.")
                        .build()
        );

    }
}
