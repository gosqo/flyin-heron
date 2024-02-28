package com.vong.manidues.auth;

import com.vong.manidues.config.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthenticationService service;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @GetMapping("/tokenValidationTest")
    public String testTokenValidation(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = request.getHeader("Authorization");
        boolean isTokenValid = jwtService.isTokenValid(
                accessToken, userDetailsService.loadUserByUsername(
                        jwtService.extractUsername(accessToken)
                )
        );

        return "The validation of your token is " + isTokenValid;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> login(
            @RequestBody AuthenticationRequest request
    ) {
        log.info("request to /api/v1/auth/authenticate " + request);
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        service.refreshToken(request, response);
    }

}
