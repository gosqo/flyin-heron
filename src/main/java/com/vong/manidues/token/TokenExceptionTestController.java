package com.vong.manidues.token;

import com.vong.manidues.config.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TokenExceptionTestController {

    private final JwtService jwtService;

    @RequestMapping("/tokenValidationTest")
    public ResponseEntity<Object> tokenValidationTest(HttpServletRequest request) {
        String authHeader = request.getHeader("authorization");
        String jwt = authHeader.substring(7);
        log.info("request to /tokenValidationTest, token is {}", authHeader);
        Map<String, String> map = new HashMap<>();
        map.put("email", jwtService.extractUsername(jwt));
        map.put("expiration", jwtService.extractClaim(jwt, Claims::getExpiration).toString());
        return ResponseEntity.ok(map);
    }
}
