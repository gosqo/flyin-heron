package com.vong.manidues.board;

import com.vong.manidues.board.dto.BoardGetResponse;
import com.vong.manidues.utility.HttpUtility;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Slf4j
public class BoardUpdateDateTests {

    @Autowired
    TestRestTemplate template;

    @Test
    public void testViewCountAffectUpdateDate() throws InterruptedException {
        String uri = "/api/v1/board/1";
        HttpEntity<String> request = HttpUtility.DEFAULT_HTTP_ENTITY;
        log.info(request.getHeaders().toString());

        ResponseEntity<BoardGetResponse> firstResponse = template.exchange(
                uri
                , HttpMethod.GET
                , request
                , BoardGetResponse.class
        );

        assertThat(firstResponse.getBody()).isNotNull();
        log.info(firstResponse.getBody().toString());

        LocalDateTime firstResponseUpdateDate = firstResponse.getBody().getUpdateDate();

        Thread.sleep(2000);

        log.info(request.getHeaders().toString());

        // without cookies in request headers.
        ResponseEntity<BoardGetResponse> secondResponse = template.exchange(
                uri
                , HttpMethod.GET
                , request
                , BoardGetResponse.class
        );

        assertThat(secondResponse.getBody()).isNotNull();
        log.info(secondResponse.getBody().toString());

        LocalDateTime secondResponseUpdateDate = secondResponse.getBody().getUpdateDate();

        assertThat(firstResponseUpdateDate).isEqualTo(secondResponseUpdateDate);
    }
}
