package com.vong.manidues.config;

import com.vong.manidues.filter.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final HttpAccessLogFilter httpAccessLogFilter;
    private final BlacklistFilter blacklistFilter;
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final LogoutHandler logoutHandler;
    private final JwtExceptionFilter jwtExceptionFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .headers(AbstractHttpConfigurer::disable)

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authenticationProvider(authenticationProvider)
                .addFilterBefore(httpAccessLogFilter, LogoutFilter.class)
                .addFilterBefore(blacklistFilter, LogoutFilter.class)
                .addFilterBefore(jwtExceptionFilter, LogoutFilter.class)
                .addFilterBefore(jwtAuthFilter, LogoutFilter.class)
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer ->
                        httpSecurityExceptionHandlingConfigurer
                                .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                )

                .logout((logout) ->
                        logout
                                .logoutUrl("/api/v1/auth/logout")
                                .addLogoutHandler(logoutHandler)
                                .logoutSuccessHandler(
                                        (request, response, authentication) ->
                                                SecurityContextHolder.clearContext()
                                )
                );
        return http.build();
    }
}
