package com.gosqo.flyinheron.global.config;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CorsConfig {
    public static CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        String[] origins = {
                "http://localhost"
                , "http://localhost:3000"
                , "http://127.0.0.1:3000"

                , "https://localhost"
                , "https://localhost:3000"
                , "https://127.0.0.1:3000"

                , "https://flyin-heron.duckdns.org"
        };
        String[] methods = {"GET", "POST", "PUT", "DELETE"};

        List<String> allowedOrigins = Arrays.stream(origins).toList();
        List<String> allowedMethods = Arrays.stream(methods).toList();

        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(allowedMethods);
        config.setAllowedHeaders(Collections.singletonList("*"));
        config.setAllowCredentials(true);

        source.registerCorsConfiguration("/api/**", config);

        return source;
    }
}
