package com.vong.manidues.config;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
// 매 요청 마다 필터를 거치도록 extends
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 헤더 인가가 비었거나, 'Bearer ' 로 시작하지 않는다면 다음 필터로.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 헤더 인가에 토큰이 있고, 'Bearer ' 로 시작하면,
        jwt = authHeader.substring(7); // 'Bearer ' 자르고
        try {
            userEmail = jwtService.extractUserEmail(jwt);// 토큰에서 userEmail 추출;
            // userEmail 이 null 이 아니고, 시큐리티 컨텍스트 홀더에 인증이 null
            // (요청 헤더에 토큰을 담은 최초의 요청일 시(SecurityContextHolder.SecurityContext.Authentication == null), SecurityContextHolder 에 해당 토큰의 Authentication 을 추가)
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                // Principal, Credential, Authorities 매개변수로 UsernamePasswordAuthenticationToken 타입 인스턴스 authToken 생성.
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken( // Authentication 의 구현체 UsernamePasswordAuthenticationToken
                        userDetails,
                        null,
                        userDetails.getAuthorities() // userDetails 객체의 getAuthorities()
                );

                // UsernamePasswordAuthenticationToken 인스턴스인 authToken 에 요청 정보를 매개변수로 전달 해 Details 세팅
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                // authToken 을 SecurityContextHolder 에 세팅하고
                SecurityContextHolder.getContext().setAuthentication(authToken); // SecurityContext 안에 Authentication 타입으로 담긴다.
            }
        } catch (ExpiredJwtException | SignatureException | MalformedJwtException | DecodingException e) {
            if (e instanceof ExpiredJwtException) {

                log.info(e.getMessage());

            } else {

                log.info("""
                                Auth Service caught error: {}
                                    Ip address is: {}
                                    User-Agent is: {}
                                """,
                        e.getMessage(),
                        request.getRemoteAddr(),
                        request.getHeader("User-Agent")
                );

            }
            response.setStatus(401);
        }

        // 다음 필터로 넘김?
        filterChain.doFilter(request, response);
    }
}
