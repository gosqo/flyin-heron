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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class BoardRestTemplateTests {

    @Autowired
    TestRestTemplate template;

    @Test
    public void getBoard() throws Exception {
        HttpEntity<String> request = new HttpEntity<>(MvcUtility.DEFAULT_HEADER);

        ResponseEntity<BoardGetResponse> response = template.exchange(
                "/api/v1/board/1"
                        , HttpMethod.GET
                        , request
                        , BoardGetResponse.class);

        log.info("\n{}\n{}"
                , request.getHeaders()
                , request.getBody()
        );

        log.info("\n{}\n{}\n{}"
                , response.getStatusCode()
                , response.getHeaders()
                , response.getBody()
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getBoardId()).isEqualTo(1L);
    }

    @Test
    public void templateTest() throws Exception {
        String rootUri = template.getRootUri();
        log.info(rootUri);
    }
}
