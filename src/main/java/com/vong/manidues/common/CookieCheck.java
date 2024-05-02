package com.vong.manidues.common;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CookieCheck {

    @GetMapping("/api/v1/cookie/list")
    public ResponseEntity<String> getCookieList(
            HttpServletResponse response
    ) {
        Cookie cookie1 = new Cookie("cookie1", "HelloCookie");
        Cookie cookie2 = new Cookie("cookie2", "HelloSecondCookie");
        cookie1.setMaxAge(60 * 60);
        cookie2.setMaxAge(60 * 60);

        response.addCookie(cookie1);
        response.addCookie(cookie2);

        return ResponseEntity.ok("Returns cookie list.");
    }

    @GetMapping("/api/v1/cookie/check")
    public ResponseEntity<String> cookieCheck() {
        return ResponseEntity.ok("There's no cookies to response.");
    }

}
