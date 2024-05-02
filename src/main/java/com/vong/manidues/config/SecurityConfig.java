package com.vong.manidues.config;

import com.vong.manidues.filter.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final IpEntryLogFilter ipEntryLogFilter;
    private final BlacklistFilter blacklistFilter;
    private final AbnormalRequestFilter abnormalRequestFilter;
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final LogoutHandler logoutHandler;
    private final JwtExceptionFilter jwtExceptionFilter;

    public static final String[] WHITE_LIST_URIS_NON_MEMBER_GET = {
            "/"
            , "/favicon.ico"
            , "/error"
            , "/error/**"
            , "/img/**"
            , "/js/**"
            , "/css/**"
            , "/login"
            , "/signUp"
            , "/api/v1/boards/**"
            , "/api/v1/board/**"
            , "/board/**"
            , "/boards/**"
            , "/api/v1/cookie/**"
    };

    public static final String[] WHITE_LIST_URIS_NON_MEMBER_POST = {
            "/api/v1/member/**"
            , "/api/v1/auth/authenticate"
            , "/error"
            , "/error/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .headers(AbstractHttpConfigurer::disable)

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.GET, WHITE_LIST_URIS_NON_MEMBER_GET).permitAll()
                        .requestMatchers(HttpMethod.POST, WHITE_LIST_URIS_NON_MEMBER_POST).permitAll()
                        .anyRequest().authenticated()
                )

                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtExceptionFilter, LogoutFilter.class)
                .addFilterBefore(ipEntryLogFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(blacklistFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(abnormalRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
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
