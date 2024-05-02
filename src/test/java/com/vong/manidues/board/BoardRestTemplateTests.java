package com.vong.manidues.board;

import com.vong.manidues.board.dto.BoardGetResponse;
import com.vong.manidues.utility.mvc.MvcUtility;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Slf4j
public class BoardRestTemplateTests {

    @Autowired
    TestRestTemplate template;

    private final HttpEntity<String> httpEntityWithoutBody = new HttpEntity<>(MvcUtility.DEFAULT_HEADER);

    @Test
    public void getBoard() throws Exception {
        ResponseEntity<BoardGetResponse> response = template.exchange(
                "/api/v1/board/1"
                        , HttpMethod.GET
                        , httpEntityWithoutBody
                        , BoardGetResponse.class);

        log.info("\n{}\n{}"
                , httpEntityWithoutBody.getHeaders()
                , httpEntityWithoutBody.getBody()
        );

        log.info("\n{}\n{}\n{}"
                , response.getStatusCode()
                , response.getHeaders()
                , response.getBody()
        );


        if (response.getStatusCode() == HttpStatusCode.valueOf(200)) {
            Long id = Objects.requireNonNull(response.getBody()).getBoardId();

            assertEquals((Long) 1L, id);
        } else {
            log.info("Check response status above.");
        }

    }
    @Test
    public void templateTest() throws Exception {
        String result = template.getRootUri();
        log.info(result);
    }
}
