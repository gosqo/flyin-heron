package com.gosqo.flyinheron.global.filter;

import com.gosqo.flyinheron.dto.ResponseBodyWriter;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilterExceptionHandler extends OncePerRequestFilter {

    private final ResponseBodyWriter responseBodyWriter;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) {
        try {
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException ex) {
            log.debug("expired jwt, user gonna request \"/api/v1/authrefresh-token\"");

            responseBodyWriter.setResponseWithBody(response
                    , 401
                    , "토큰 만료."
            );
        } catch (JwtException | NullPointerException ex) {

            if (ex instanceof JwtException) {
                log.warn("*** manipulated token *** {}: {} ***", ex.getClass().getName(), ex.getMessage());
            } else {
                log.warn(ex.getMessage(), ex);
            }

            responseBodyWriter.setResponseWithBody(response
                    , 400
                    , "올바른 접근이 아닙니다. 로그아웃 후 다시 로그인 해주십시오."
            );
        } catch (Exception ex) {
            log.warn(ex.getMessage(), ex);

            responseBodyWriter.setResponseWithBody(response
                    , 500
                    , "서버에 문제가 발생했습니다. 반복되는 문제일 경우 운영진에 연락 바랍니다."
            );
        }
    }
}
