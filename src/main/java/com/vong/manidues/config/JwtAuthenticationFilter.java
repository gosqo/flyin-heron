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

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);

        try {
            userEmail = jwtService.extractUserEmail(jwt);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (ExpiredJwtException | SignatureException | MalformedJwtException | DecodingException ex) {
            if (ex instanceof ExpiredJwtException) {

                response.setStatus(401);
                log.info("""
                                {}
                                    response with 401, client will request to "/api/v1/auth/refresh-token" with refreshToken.
                                """,
                        ex.getMessage()
                );

            } else { // ExpiredJwtException 외의 에러는 조작된 것으로 간주. response 400

                response.setStatus(400);
                log.warn("""
                                JWT Auth Filter caught error: {}
                                    === Guess that client's Token has been manipulated. ===
                                    {} {} {} {}
                                    User-Agent: {}
                                    Connection: {}
                                """
                        , ex.getMessage()
                        , request.getRemoteAddr()
                        , request.getProtocol()
                        , request.getMethod()
                        , request.getRequestURI()
                        , request.getHeader("User-Agent")
                        , request.getHeader("Connection")
                );
            }
        }
        filterChain.doFilter(request, response);
    }
}
