package com.vong.manidues.common;

import com.vong.manidues.utility.mvc.MvcUtility;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Slf4j
public class CookieCheckTests {

    @Autowired
    TestRestTemplate template;

    private final HttpHeaders defaultHeaders = MvcUtility.DEFAULT_HEADER;

    @Test
    public void checkCookieList() {
        HttpEntity<String> firstRequest = new HttpEntity<>(defaultHeaders);
        ResponseEntity<String> response = template.exchange(
                "/api/v1/cookie/list"
                , HttpMethod.GET
                , firstRequest
                , String.class
        );

        // 응답 헤더에서 리스트 형인 쿠키를 꺼낸다.
        List<String> firstResponseCookies = response.getHeaders().get("Set-Cookie");
        assert firstResponseCookies != null;

        // 요청 헤더에 넣을 쿠키 리스트를 선언한다.
        List<String> secondRequestCookies = new ArrayList<>();

        // 응답 쿠키 리스트를 순회하며 요청 헤더 쿠키에 넣을 리스트에 넣는다.
        for (String cookie : firstResponseCookies) {
            String[] cookiePair = cookie.split(";");

            secondRequestCookies.add(cookiePair[0]);
        }

        // 기존의 요청 헤더에 쿠키 리스트를 넣는다.
        defaultHeaders.addAll("Cookie", secondRequestCookies);

        // 쿠키를 넣은 오청 헤더를 사용할 HttpEntity 객체를 만든다.
        HttpEntity<String> secondRequest = new HttpEntity<>(defaultHeaders);

        // 요청한다.
        ResponseEntity<String> secondResponse = template.exchange(
                "/api/v1/cookie/check"
                , HttpMethod.GET
                , secondRequest
                , String.class
        );

        assert secondResponse.getHeaders().get("Set-Cookie") == null;

        log.info(firstResponseCookies.toString());
    }
}
