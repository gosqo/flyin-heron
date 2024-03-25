package com.vong.manidues.auth;

import com.vong.manidues.utility.JsonResponseBody;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> login(
            @Valid @RequestBody AuthenticationRequest request
    ) {
        log.info("request to /api/v1/auth/authenticate\n Email is: {}", request.getEmail());
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Object> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        if (request.getHeader("Authorization") == null) {
            log.info("POST api/v1/auth/refresh-token header.Authorization is null. non-Member request");
            return ResponseEntity.badRequest().body(
                    JsonResponseBody.builder()
                            .statusCode(400)
                            .message("refresh-token is null")
                            .additionalMessage("non-Member request")
                            .build()
            );
        }
        log.info("request to /api/v1/auth/refresh-token\n token is: {}", request.getHeader("Authorization"));
        return ResponseEntity.ok(service.refreshToken(request, response));
    }

}
