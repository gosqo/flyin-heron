package com.vong.manidues.nginx;

import com.vong.manidues.web.HttpUtility;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class RemoteNginxFilterTest {
    private static final Logger log = LoggerFactory.getLogger(RemoteNginxFilterTest.class);
    private static final TestRestTemplate restTemplate = new TestRestTemplate();
    private static final String url = "https://flyin-heron.duckdns.org";

    @Test
    void passNginxFilterTest() {
        ResponseEntity<String> response = restTemplate.exchange(
                HttpUtility.buildGetRequest(url)
                , String.class
        );

        log.info(String.valueOf(response));
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void nullUserAgentHeaderTest() {
        HttpHeaders headers = new HttpHeaders();
//        headers.set("Connection", "Keep-Alive"); // 명시하지 않아도 HTTP/1.1 프로토콜, nginx 특성상 keep-alive 로 간주.

        Assertions.assertThatThrownBy(() ->
                restTemplate.exchange(
                        HttpUtility.buildGetRequest(headers, url)
                        , String.class
                ));
    }

    @Test
    void closeConnectionHeaderTest() {
        /*
         * "Connection" header 따로 지정하지 않아도 기본적으로 연결을 유지하려는 HTTP/1.1 프로토콜
         * 클라이언트가 명시적으로 close 헤더를 보내지 않는 한, Nginx 는 연결 유지를 위해 keep-alive 로 간주.
         * 이와 같은 이유로 "Connection" null 에 대한 테스트는 어려운 것으로 보임.
         * */
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Mozilla");
        headers.set("Connection", "close");

        Assertions.assertThatThrownBy(() ->
                restTemplate.exchange(
                        HttpUtility.buildGetRequest(headers, url)
                        , String.class
                ));
    }
}
