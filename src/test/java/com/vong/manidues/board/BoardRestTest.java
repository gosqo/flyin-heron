package com.vong.manidues.board;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vong.manidues.board.dto.BoardGetResponse;
import com.vong.manidues.board.dto.BoardRegisterRequest;
import com.vong.manidues.board.dto.BoardRegisterResponse;
import com.vong.manidues.member.MemberRepository;
import com.vong.manidues.token.TokenUtility;
import com.vong.manidues.web.HttpUtility;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static com.vong.manidues.auth.AuthenticationFixture.MEMBER_ID;
import static com.vong.manidues.web.HttpUtility.buildGetRequest;
import static com.vong.manidues.web.HttpUtility.buildPostRequest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Slf4j
public class BoardRestTest {

    @Autowired
    private TestRestTemplate template;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final TokenUtility tokenUtility;

    @Autowired
    public BoardRestTest(
            BoardRepository boardRepository
            , MemberRepository memberRepository
            , TokenUtility tokenUtility
    ) {
        this.boardRepository = boardRepository;
        this.memberRepository = memberRepository;
        this.tokenUtility = tokenUtility;
    }

    @BeforeEach
    void setUp() {
        boardRepository.save(Board.builder()
                .title("hello")
                .content("testing updateDate.")
                .member(memberRepository.findById(1L).orElseThrow())
                .build()
        );
    }

    @Test
    public void getBoard() throws Exception {
        final var request = buildGetRequest("/api/v1/board/1");
        final var response = template.exchange(request, BoardGetResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getBoardId()).isEqualTo(1L);
    }

    @Test
    public void testViewCountAffectUpdateDate() throws InterruptedException {
        final var request = buildGetRequest("/api/v1/board/1");
        final var firstResponse = template.exchange(request, BoardGetResponse.class);

        assertThat(firstResponse.getBody()).isNotNull();
        final var firstResponseUpdateDate = firstResponse.getBody().getUpdateDate();

        Thread.sleep(1000);
        final var secondResponse = template.exchange(request, BoardGetResponse.class);

        assertThat(secondResponse.getBody()).isNotNull();
        final var secondResponseUpdateDate = secondResponse.getBody().getUpdateDate();

        assertThat(firstResponseUpdateDate).isEqualTo(secondResponseUpdateDate);
    }

    @Test
    public void boardPostNormally_ItShouldBeLike() throws JsonProcessingException {

        // given
        // to build HTTP headers with Authorization
        final var token = tokenUtility.issueAccessTokenOnTest(MEMBER_ID);
        HttpHeaders headers = HttpUtility.buildPostHeadersWithAuth(token);
        // build body, JSON 형태의 스트링으로 보내지 않고 객체로 전달해도 테스트 가능.
        BoardRegisterRequest body = BoardRegisterRequest.builder()
                .title("Board from boardPostTests")
                .content("test")
                .build();
        final var request = buildPostRequest(headers, body, "/api/v1/board");

        // when
        final var response = template.exchange(request, BoardRegisterResponse.class);

        assertThat(response.getBody()).isNotNull();
        Board registeredBoard = boardRepository
                .findById(response.getBody().getId()).orElseThrow();

        LocalDateTime dateTime1 = registeredBoard.getRegisterDate();
        LocalDateTime dateTime2 = registeredBoard.getUpdateDate();
        long nanoGap = ChronoUnit.NANOS.between(dateTime1, dateTime2);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(registeredBoard.getMember().getId()).isEqualTo(MEMBER_ID);
        assertThat(registeredBoard.getTitle()).isEqualTo(body.getTitle());
        assertThat(registeredBoard.getContent()).isEqualTo(body.getContent());
        assertThat(registeredBoard.getViewCount()).isEqualTo(0);
        assertThat(nanoGap).isLessThan(1_000_000L);
    }
}
