package com.gosqo.flyinheron.acceptance;

import com.gosqo.flyinheron.domain.Board;
import com.gosqo.flyinheron.dto.board.BoardGetResponse;
import com.gosqo.flyinheron.dto.board.BoardRegisterRequest;
import com.gosqo.flyinheron.dto.board.BoardRegisterResponse;
import com.gosqo.flyinheron.global.data.TestDataRemover;
import com.gosqo.flyinheron.repository.BoardRepository;
import com.gosqo.flyinheron.repository.MemberRepository;
import com.gosqo.flyinheron.service.BoardService;
import com.gosqo.flyinheron.service.JwtService;
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
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

class BoardTest extends SpringBootTestBase {
    private final JwtService jwtService;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;

    @Autowired
    BoardTest(
            TestRestTemplate template
            , JwtService jwtService
            , MemberRepository memberRepository
            , BoardRepository boardRepository
            , TestDataRemover remover
    ) {
        super(template, remover);
        this.jwtService = jwtService;
        this.memberRepository = memberRepository;
        this.boardRepository = boardRepository;
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
        member = memberRepository.save(buildMember());
        boards = boardRepository.saveAll(buildBoards());
    }

    @Nested
    @DisplayName("board acceptance")
    class IntegratedBoardTest {
        @Test
        public void view_count_does_not_affect_to_update_date() {
            Long boardId = boards.get(0).getId();
            String uri = String.format("/api/v1/board/%d", boardId);

            final var request = RequestEntity
                    .get(uri)
                    .build();

            final var firstResponse = template.exchange(request, BoardGetResponse.class);

            assertThat(firstResponse.getBody()).isNotNull();
            final var firstResponseUpdateDate = firstResponse.getBody().getUpdateDate();

            final var secondResponse = template.exchange(request, BoardGetResponse.class);

            assertThat(secondResponse.getBody()).isNotNull();
            final var secondResponseUpdateDate = secondResponse.getBody().getUpdateDate();

            assertThat(firstResponseUpdateDate).isEqualTo(secondResponseUpdateDate);
        }

        @Test
        @DisplayName("board GET is like.")
        public void getBoard() {
            Board board = boards.get(0);
            Long boardId = board.getId();
            String uri = String.format("/api/v1/board/%d", boardId);

            final var request = RequestEntity
                    .get(uri)
                    .build();

            final var response = template.exchange(request, BoardGetResponse.class);
            final var responseBody = Objects.requireNonNull(response.getBody());

            assertThat(responseBody.getBoardId()).isEqualTo(boardId);
            assertThat(responseBody.getTitle()).isEqualTo(board.getTitle());
            assertThat(responseBody.getContent()).isEqualTo(board.getContent());
            assertThat(responseBody.getMember().profileImage()).isNull();
        }

        @Test
        @DisplayName("board POST is like.")
        public void boardPostNormally_ItShouldBeLike() {
            // given
            final var accessToken = jwtService.generateAccessToken(member);
            final var bearerAccessToken = "Bearer " + accessToken;
            final var body = BoardRegisterRequest.builder()
                    .title("title")
                    .content("content")
                    .build();

            final var request = RequestEntity
                    .post("/api/v1/board")
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

            final var request = RequestEntity
                    .get(uri)
                    .build();

            final var response = template.exchange(request, BoardGetResponse.class);
            final var responseCookie = Objects.requireNonNull(response.getHeaders().get("Set-Cookie"));

            assertThat(responseCookie.stream()
                    .filter(item -> item.startsWith(BoardService.BOARD_VIEWS_COOKIE_NAME))
                    .map(item -> item.split(";"))
                    .map(item -> item[0])
                    .findFirst()
                    .orElseThrow()
                    .equals(expectedCookie)
            ).isTrue();
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

            final var firstRequest = RequestEntity
                    .get(firstRequestUri)
                    .headers(firstRequestHeaders)
                    .build();

            final var firstResponse = template.exchange(firstRequest, BoardGetResponse.class);

            // Parse cookies in response headers
            // Prepare to place the cookie value in the next request header.
            final var firstResponseCookies = firstResponse.getHeaders().get("Set-Cookie");
            assertThat(firstResponseCookies).isNotNull();
            final var cookiesToSend = getCookieListFromResponse(firstResponseCookies);

            // second
            final var secondRequestHeaders = new HttpHeaders();
            secondRequestHeaders.addAll("Cookie", cookiesToSend);
            final var secondRequest = RequestEntity
                    .get(secondRequestUri)
                    .headers(secondRequestHeaders)
                    .build();

            final var secondResponse = template.exchange(secondRequest, BoardGetResponse.class);
            final var secondResponseCookies = Objects.requireNonNull(secondResponse.getHeaders().get("Set-Cookie"));

            assertThat(secondResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(secondResponseCookies.stream()
                    .filter(item -> item.startsWith(BoardService.BOARD_VIEWS_COOKIE_NAME))
                    .map(item -> item.split(";"))
                    .map(item -> item[0])
                    .findFirst()
                    .orElseThrow()
                    .equals(expectedFinalCookie)
            ).isTrue();
        }
    }
}
