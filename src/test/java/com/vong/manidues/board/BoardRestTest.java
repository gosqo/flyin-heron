package com.vong.manidues.board;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vong.manidues.board.dto.BoardGetResponse;
import com.vong.manidues.board.dto.BoardRegisterResponse;
import com.vong.manidues.member.MemberRepository;
import com.vong.manidues.token.TokenUtility;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.temporal.ChronoUnit;

import static com.vong.manidues.auth.AuthenticationFixture.MEMBER_ENTITY;
import static com.vong.manidues.board.BoardDtoUtility.buildBoardRegisterRequest;
import static com.vong.manidues.web.HttpUtility.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
@Slf4j
public class BoardRestTest {
    private final TestRestTemplate template;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final TokenUtility tokenUtility;

    @Autowired
    public BoardRestTest(
            TestRestTemplate template
            , BoardRepository boardRepository
            , MemberRepository memberRepository
            , TokenUtility tokenUtility
    ) {
        this.template = template;
        this.boardRepository = boardRepository;
        this.memberRepository = memberRepository;
        this.tokenUtility = tokenUtility;
    }

    @BeforeEach
    void setUp() {
        boardRepository.save(Board.builder()
                .title("hello")
                .content("testing")
                .member(memberRepository.findById(1L).orElseThrow())
                .build()
        );
    }

    @AfterEach
    void tearDown() {
        log.info(boardRepository.findAll().toString());
    }

    @Test
    public void getBoard() {
        final var request = buildGetRequest("/api/v1/board/1");
        final var response = template.exchange(request, BoardGetResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getBoardId()).isEqualTo(1L);
        assertThat(response.getBody().getTitle()).isEqualTo("hello");
        assertThat(response.getBody().getContent()).isEqualTo("testing");
    }

    @Test
    public void testViewCountAffectUpdateDate() {
        final var request = buildGetRequest("/api/v1/board/1");
        final var firstResponse = template.exchange(request, BoardGetResponse.class);

        assertThat(firstResponse.getBody()).isNotNull();
        final var firstResponseUpdateDate = firstResponse.getBody().getUpdateDate();

        final var secondResponse = template.exchange(request, BoardGetResponse.class);

        assertThat(secondResponse.getBody()).isNotNull();
        final var secondResponseUpdateDate = secondResponse.getBody().getUpdateDate();

        assertThat(firstResponseUpdateDate).isEqualTo(secondResponseUpdateDate);
    }

    @Test
    public void boardPostNormally_ItShouldBeLike() throws JsonProcessingException {

        // given
        // to build HTTP headers with Authorization
        final var accessToken = tokenUtility.buildToken(MEMBER_ENTITY);
        final var BearerAccessToken = "Bearer " + accessToken;
        final var headers = buildPostHeadersWithAuth(BearerAccessToken);
        // build body, JSON 형태의 스트링으로 보내지 않고 객체로 전달해도 테스트 가능.
        final var body = buildBoardRegisterRequest();
        final var request = buildPostRequest(headers, body, "/api/v1/board");

        // when
        final var response = template.exchange(request, BoardRegisterResponse.class);

        assertThat(response.getBody()).isNotNull();
        final var registeredBoard = boardRepository.findById(response.getBody().getId()).orElseThrow();

        final var dateTime1 = registeredBoard.getRegisterDate();
        final var dateTime2 = registeredBoard.getUpdateDate();
        long nanoGap = ChronoUnit.NANOS.between(dateTime1, dateTime2);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(registeredBoard.getMember().getId()).isEqualTo(MEMBER_ENTITY.getId());
        assertThat(registeredBoard.getTitle()).isEqualTo(body.getTitle());
        assertThat(registeredBoard.getContent()).isEqualTo(body.getContent());
        assertThat(registeredBoard.getViewCount()).isEqualTo(0);
        assertThat(nanoGap).isLessThan(1_000_000L);
    }
}
