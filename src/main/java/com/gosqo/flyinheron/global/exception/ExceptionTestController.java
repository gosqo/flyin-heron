package com.gosqo.flyinheron.global.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.impl.DefaultJwsHeader;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestController
@RequestMapping("/api/v1/exception")
public class ExceptionTestController {

    @GetMapping("/bad-credentials")
    public void throwBadCredentialsException() {
        // 400
        throw new BadCredentialsException("bad credentials");
    }

    @GetMapping("/expired-jwt")
    public void throwExpireJwtException() {
        // 401
        throw new ExpiredJwtException(
                new DefaultJwsHeader()
                , new DefaultClaims()
                , "expired."
        );
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/access-denied")
    public void throwAuthRequired() {
        // 403 without token, throws AccessDeniedException
    }

    @GetMapping("/no-resource-found")
    public void throwNoResourceFoundException() throws NoResourceFoundException {
        // 404
        throw new NoResourceFoundException(HttpMethod.GET, "/api/v1/exception/no-resource-found");
    }

    @GetMapping("/null-pointer")
    public void throwGetNullPointerException() {
        // 500
        throw new NullPointerException("hello, this NPE thrown by exception test controller.");
    }

    @PostMapping("/null-pointer")
    public void throwPostNullPointerException() {
        // 500
        throw new NullPointerException("hello, this NPE thrown by exception test controller.");
    }
}
