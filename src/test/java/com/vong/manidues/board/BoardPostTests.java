package com.vong.manidues.board;

import com.vong.manidues.board.dto.BoardRegisterRequest;
import com.vong.manidues.board.dto.BoardRegisterResponse;
import com.vong.manidues.web.HttpUtility;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class BoardPostTests {

    private final TestRestTemplate template;
    private final HttpUtility httpUtility;
    private final BoardRepository boardRepository;

    @Autowired
    public BoardPostTests(TestRestTemplate template, HttpUtility httpUtility, BoardRepository boardRepository) {
        this.template = template;
        this.httpUtility = httpUtility;
        this.boardRepository = boardRepository;
    }

    @Test
    public void boardPostNormally_ItShouldBeLike() {

        // given
        String uri = "/api/v1/board";
        HttpMethod method = HttpMethod.POST;
        HttpEntity<BoardRegisterRequest> request;
        Class<BoardRegisterResponse> responseType = BoardRegisterResponse.class;

        // to build HTTP headers with Authorization
        Long memberId = 1L;
        HttpHeaders requestHeaders = httpUtility.headersWithAuthorization(memberId);

        // build body, JSON 형태의 스트링으로 보내지 않고 객체로 전달해도 테스트 가능.
        BoardRegisterRequest requestBody = BoardRegisterRequest.builder()
                .title("Board from boardPostTests")
                .content("test")
                .build();

        // assign HttpEntity as request
        request = new HttpEntity<>(requestBody, requestHeaders);

        log.info(request.toString());

        // when
        ResponseEntity<BoardRegisterResponse> response =
                template.exchange(uri, method, request, responseType);

        log.info(response.toString());

        assertThat(response.getBody()).isNotNull();
        Board registeredBoard = boardRepository
                .findById(response.getBody().getId()).orElseThrow();

        LocalDateTime dateTime1 = registeredBoard.getRegisterDate();
        LocalDateTime dateTime2 = registeredBoard.getUpdateDate();
        long nanoGap = ChronoUnit.NANOS.between(dateTime1, dateTime2);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(registeredBoard.getMember().getId()).isEqualTo(memberId);
        assertThat(registeredBoard.getTitle()).isEqualTo(requestBody.getTitle());
        assertThat(registeredBoard.getContent()).isEqualTo(requestBody.getContent());
        assertThat(registeredBoard.getViewCount()).isEqualTo(0);
        assertThat(nanoGap).isLessThan(1_000_000L);
    }
}
