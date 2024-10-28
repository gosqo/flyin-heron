package com.gosqo.flyinheron.global.filter;

import com.gosqo.flyinheron.dto.ResponseBodyWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) {
        ResponseBodyWriter responseBodyWriter = new ResponseBodyWriter();

        if (authException instanceof BadCredentialsException) {
            responseBodyWriter.setResponseWithBody(
                    response,
                    400,
                    "아이디 혹은 비밀번호를 확인해주세요."
            );
        } else if (authException instanceof InsufficientAuthenticationException) {
            responseBodyWriter.setResponseWithBody(
                    response,
                    401,
                    "인증정보가 필요합니다."
            );
            log.info("""
                            *** Request to URI require authentication. *** response with {}
                                AuthHeader: {}
                            """
                    , response.getStatus()
                    , request.getHeader("Authorization")
            );
        }
    }
}
