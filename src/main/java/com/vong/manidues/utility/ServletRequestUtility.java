package com.vong.manidues.utility;

import com.vong.manidues.config.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServletRequestUtility {

    private final JwtService jwtService;

    public String extractEmailFromHeader(HttpServletRequest servletRequest) {

        String authHeader = servletRequest.getHeader("Authorization");
        String accessToken = authHeader.substring(7);

        return jwtService.extractUserEmail(accessToken);
    }

    public String extractJwtFromHeader(HttpServletRequest servletRequest) {

        String authHeader = servletRequest.getHeader("Authorization");

        return authHeader.substring(7);

    }

}
