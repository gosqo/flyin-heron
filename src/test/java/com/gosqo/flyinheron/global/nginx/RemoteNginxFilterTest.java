package com.gosqo.flyinheron.global.nginx;

import com.gosqo.flyinheron.global.utility.HeadersUtility;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RemoteNginxFilterTest {
    private static final Logger log = LoggerFactory.getLogger(RemoteNginxFilterTest.class);
    private static final String TARGET_URL = "https://flyin-heron.duckdns.org";
    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @Test
    void pass_case() {
        RequestEntity<Void> request = RequestEntity
                .get(TARGET_URL)
                .headers(HeadersUtility.buildNginxGetHeaders())
                .build();

        ResponseEntity<String> response = restTemplate.exchange(request, String.class);

        log.info(String.valueOf(response));
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Nested
    class Cannot_pass_with_HTTP_headers_with {

        @Test
        void null_User_Agent() {
            HttpHeaders headers = new HttpHeaders();
//        headers.set("Connection", "Keep-Alive"); // 명시하지 않아도 HTTP/1.1 프로토콜, nginx 특성상 keep-alive 로 간주.

            RequestEntity<Void> request = RequestEntity
                    .get(TARGET_URL)
                    .headers(headers)
                    .build();

            Assertions.assertThatThrownBy(() ->
                    restTemplate.exchange(request, String.class));
        }

        @Test
        void close_Connection() {
            /*
             * "Connection" header 따로 지정하지 않아도 기본적으로 연결을 유지하려는 HTTP/1.1 프로토콜
             * 클라이언트가 명시적으로 close 헤더를 보내지 않는 한, Nginx 는 연결 유지를 위해 keep-alive 로 간주.
             * 이와 같은 이유로 "Connection" null 에 대한 테스트는 어려운 것으로 보임.
             * */
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla");
            headers.set("Connection", "close");

            RequestEntity<Void> request = RequestEntity
                    .get(TARGET_URL)
                    .headers(headers)
                    .build();

            Assertions.assertThatThrownBy(() ->
                    restTemplate.exchange(request, String.class));
        }
    }
}
