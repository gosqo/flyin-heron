package com.vong.manidues.integrated;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vong.manidues.domain.Board;
import com.vong.manidues.dto.board.BoardGetResponse;
import com.vong.manidues.dto.board.BoardRegisterRequest;
import com.vong.manidues.dto.board.BoardRegisterResponse;
import com.vong.manidues.global.utility.HttpUtility;
import com.vong.manidues.repository.BoardRepository;
import com.vong.manidues.repository.CommentRepository;
import com.vong.manidues.repository.MemberRepository;
import com.vong.manidues.repository.TokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static com.vong.manidues.global.utility.HttpUtility.buildGetRequestEntity;
import static org.assertj.core.api.Assertions.assertThat;

class BoardTest extends SpringBootTestBase {
    private final TestTokenBuilder tokenBuilder;

    @Autowired
    public BoardTest(
            MemberRepository memberRepository,
            TokenRepository tokenRepository,
            BoardRepository boardRepository,
            CommentRepository commentRepository,
            TestRestTemplate template,
            TestTokenBuilder tokenBuilder
    ) {
        super(memberRepository, boardRepository, commentRepository, tokenRepository, template);
        this.tokenBuilder = tokenBuilder;
    }

    private static ArrayList<String> getCookieListFromResponse(List<String> cookiesFromResponse) {
        final var cookiesToSend = new ArrayList<String>();
        for (String cookie : cookiesFromResponse) {
            String[] cookiePairs = cookie.split(";");
            cookiesToSend.add(cookiePairs[0]);
        }
        return cookiesToSend;
    }

    @BeforeEach
    void setUp() {
        initBoards();
    }

    @Nested
    @DisplayName("board integrated")
    class IntegratedBoardTest {
        @Test
        @DisplayName("view count does not affect to update date.")
        public void testViewCountAffectUpdateDate() {
            Long boardId = boards.get(0).getId();
            String uri = String.format("/api/v1/board/%d", boardId);
            final var request = HttpUtility.buildGetRequestEntity(uri);
            final var firstResponse = template.exchange(request, BoardGetResponse.class);

            assertThat(firstResponse.getBody()).isNotNull();
            final var firstResponseUpdateDate = firstResponse.getBody().getUpdateDate();

            final var secondResponse = template.exchange(request, BoardGetResponse.class);

            assertThat(secondResponse.getBody()).isNotNull();
            final var secondResponseUpdateDate = secondResponse.getBody().getUpdateDate();

            assertThat(firstResponseUpdateDate).isEqualTo(secondResponseUpdateDate);
        }

        @Test
        @DisplayName("E2E board GET is like.")
        public void getBoard() {
            Board board = boards.get(0);
            Long boardId = board.getId();
            String uri = String.format("/api/v1/board/%d", boardId);

            final var request = HttpUtility.buildGetRequestEntity(uri);
            final var response = template.exchange(request, BoardGetResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getBoardId()).isEqualTo(boardId);
            assertThat(response.getBody().getTitle()).isEqualTo(board.getTitle());
            assertThat(response.getBody().getContent()).isEqualTo(board.getContent());
        }

        @Test
        @DisplayName("E2E board POST is like.")
        public void boardPostNormally_ItShouldBeLike() throws JsonProcessingException {
            // given
            // to build HTTP headers with Authorization
            final var accessToken = tokenBuilder.buildToken(member);
            final var bearerAccessToken = "Bearer " + accessToken;
            // build body, JSON 형태의 스트링으로 보내지 않고 객체로 전달해도 테스트 가능.
            final var body = BoardRegisterRequest.builder()
                    .title("title")
                    .content("content")
                    .build();
//            final var request = buildPostRequest(headers, body, "/api/v1/board");
            final var request = RequestEntity.post("/api/v1/board")
                    .header("Authorization", bearerAccessToken)
                    .body(body);

            // when
            final var response = template.exchange(request, BoardRegisterResponse.class);

            assertThat(response.getBody()).isNotNull();
            final var registeredBoard = boardRepository.findById(response.getBody().getId()).orElseThrow();

            final var dateTime1 = registeredBoard.getRegisterDate();
            final var dateTime2 = registeredBoard.getUpdateDate();
            long nanoGap = ChronoUnit.NANOS.between(dateTime1, dateTime2);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(registeredBoard.getMember().getId()).isEqualTo(member.getId());
            assertThat(registeredBoard.getTitle()).isEqualTo(body.getTitle());
            assertThat(registeredBoard.getContent()).isEqualTo(body.getContent());
            assertThat(registeredBoard.getViewCount()).isEqualTo(0);
            assertThat(nanoGap).isLessThan(1_000_000L);
        }
    }

    @Nested
    @DisplayName("board cookie")
    class BoardCookieTest {

        @Test
        @DisplayName("When client has no cookie 'bbv', initialize cookie 'bbv'.")
        public void initializeBbvCookie() {
            final var boardId = boards.get(0).getId();
            final var uri = String.format("/api/v1/board/%d", boardId);
            final var expectedCookie = String.format("bbv=%d", boardId);
            final var request = HttpUtility.buildGetRequestEntity(uri);
            final var response = template.exchange(request, BoardGetResponse.class);

            assertThat(response.getHeaders().get("Set-Cookie")).isNotNull();
            assertThat(response.getHeaders().get("Set-Cookie").get(0)).startsWith(expectedCookie);
        }

        @Test
        @DisplayName("When client has cookie 'bbv', can add values to cookie 'bbv'.")
        public void canSetCookie() {
            final var firstBoardId = boards.get(0).getId();
            final var secondBoardId = boards.get(1).getId();
            final var thirdBoardId = boards.get(2).getId();

            final var firstRequestCookie = String.format("bbv=%d", firstBoardId);
            final var firstRequestUri = String.format("/api/v1/board/%d", secondBoardId);
            final var secondRequestUri = String.format("/api/v1/board/%d", thirdBoardId);
            final var expectedFinalCookie = String.format("bbv=%d/%d/%d", firstBoardId, secondBoardId, thirdBoardId);

            // first
            final var firstRequestHeaders = new HttpHeaders();
            firstRequestHeaders.add("Cookie", firstRequestCookie);
            final var firstRequest = buildGetRequestEntity(firstRequestHeaders, firstRequestUri);
            final var firstResponse = template.exchange(firstRequest, BoardGetResponse.class);

            // Parse cookies in response headers
            // Prepare to place the cookie value in the next request header.
            final var firstResponseCookies = firstResponse.getHeaders().get("Set-Cookie");
            assertThat(firstResponseCookies).isNotNull();
            final var cookiesToSend = getCookieListFromResponse(firstResponseCookies);

            // second
            final var secondRequestHeaders = new HttpHeaders();
            secondRequestHeaders.addAll("Cookie", cookiesToSend);
            final var secondRequest = buildGetRequestEntity(secondRequestHeaders, secondRequestUri);
            final var secondResponse = template.exchange(secondRequest, BoardGetResponse.class);
            final var secondResponseCookies = secondResponse.getHeaders().get("Set-Cookie");

            assertThat(secondResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(secondResponseCookies).isNotNull();
            assertThat(secondResponseCookies.get(0)).startsWith(expectedFinalCookie);
        }
    }
}
