package com.vong.manidues.utility;

import com.vong.manidues.token.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServletRequestUtility {

    private final JwtService jwtService;

    public String extractJwtFromHeader(HttpServletRequest servletRequest) {
        return servletRequest.getHeader("Authorization").substring(7);
    }

    public String extractEmailFromHeader(HttpServletRequest servletRequest) {
        return jwtService.extractUserEmail(extractJwtFromHeader(servletRequest));
    }
}
