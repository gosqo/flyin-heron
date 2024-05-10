package com.vong.manidues.web;

import jakarta.servlet.http.Cookie;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.Objects;

@Getter
@Component
@Slf4j
public class MvcUtility {

    public static final HttpHeaders DEFAULT_HEADER = new HttpHeaders();

    public static final HttpHeaders DEFAULT_POST_HEADER = new HttpHeaders(DEFAULT_HEADER);

    static {
        DEFAULT_HEADER.add("Connection", "keep-alive");
        DEFAULT_HEADER.add("User-Agent", "Mozilla");

        DEFAULT_POST_HEADER.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
    }

    public void logResultHeaders(MvcResult requestResult) {
        Arrays.stream(Objects.requireNonNull(requestResult.getRequest().getCookies()))
                .forEach(cookie -> log.info("{}: {}"
                        , cookie.getName()
                        , cookie.getValue()
                ));
    }

    public Cookie findCookieValueLimit() {
        Cookie cookie = new Cookie("bbv", null);
        StringBuilder bbvValue = new StringBuilder();

        //  i ==> 2 부터 시작.
        for (int i = 2; bbvValue.toString().getBytes().length < 5000; i++) {
            if (i == 2) {
                bbvValue.append(i);
                continue;
            }
            bbvValue.append(String.format("/%d", i));
        }

        cookie.setValue(bbvValue.toString());

        return cookie;
    }
}
