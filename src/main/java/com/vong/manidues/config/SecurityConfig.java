package com.vong.manidues.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.security.Key;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        log.info("=== filterChain(HttpSecurity http) invoked.");

        http
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .headers().disable()
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/error", "/favicon.ico", "/resources/static/img/*", "/js/member/login.js", "/test", "/login", "/login/process", "/member", "/").permitAll()
                        .anyRequest().authenticated()
                )
//                .formLogin(formLogin ->
//                        formLogin
//                                .loginPage("/login")
//                                .usernameParameter("email")
//                                .passwordParameter("password")
//                                .loginProcessingUrl("/login/process")
//                                .defaultSuccessUrl("/", true)
//                                .failureUrl("/login")
//                )
                .logout(logoutConfig ->
                        logoutConfig.logoutSuccessUrl("/login")
                );

        return http.build();
    }
}
