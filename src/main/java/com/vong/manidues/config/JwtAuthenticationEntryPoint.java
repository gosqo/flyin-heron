package com.vong.manidues.config;

import com.vong.manidues.utility.HttpResponseWithBody;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * 1. 액세스 토큰 없이 JwtAuthenticationFilter 를 거칠 때 호출 (페이지 최초 방문, js에 작성한 비동기 통신 아님.)
     *
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        String message = authException.getMessage();
        HttpResponseWithBody responseWithBody = new HttpResponseWithBody();

//        another message case: Full authentication is required to access this resource
        if (message.equals("Bad credentials")) {
            responseWithBody.jsonResponse(response, 400, "아이디 혹은 비밀번호를 확인해주세요.");
        } else {
            responseWithBody.jsonResponse(response, 401, "인증정보가 필요합니다.");
        }


    }
}
