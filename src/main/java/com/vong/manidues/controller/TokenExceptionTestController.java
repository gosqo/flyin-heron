package com.vong.manidues.controller;

import com.vong.manidues.service.ClaimExtractor;
import com.vong.manidues.global.utility.AuthHeaderUtility;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TokenExceptionTestController {
    private final ClaimExtractor claimExtractor;

    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping("/tokenValidationTest")
    public ResponseEntity<Object> tokenValidationTest(HttpServletRequest request) {
        String jwt = AuthHeaderUtility.extractJwt(request);
        Map<String, String> map = new HashMap<>();

        map.put("email", claimExtractor.extractUserEmail(jwt));
        map.put("expiration", claimExtractor.extractExpiration(jwt).toString());

        log.info("""
                        requesting member email is: {}"""
                , map.get("email"));

        return ResponseEntity.status(200).body(map);
    }
}
