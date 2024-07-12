package com.vong.manidues.domain.board;

import com.vong.manidues.domain.board.Board;
import com.vong.manidues.domain.board.BoardRepository;
import com.vong.manidues.domain.board.dto.BoardGetResponse;
import com.vong.manidues.domain.member.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static com.vong.manidues.web.HttpUtility.buildDefaultHeaders;
import static com.vong.manidues.web.HttpUtility.buildGetRequest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class BoardCookieRestTemplateTest {

    @Autowired
    TestRestTemplate template;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public BoardCookieRestTemplateTest(
            BoardRepository boardRepository
            , MemberRepository memberRepository
    ) {
        this.boardRepository = boardRepository;
        this.memberRepository = memberRepository;
    }

    @BeforeEach
    void setUp() {
        for (int i = 1; i < 4; i++) {
            boardRepository.save(Board.builder()
                    .title("hello" + i)
                    .content("testing updateDate.")
                    .member(memberRepository.findById(1L).orElseThrow())
                    .build()
            );
        }
    }

    @AfterEach
    void tearDown() {
        boardRepository.deleteAll();
    }

    @Test
    public void initializeBbvCookie() {
        final var request = buildGetRequest("/api/v1/board/1");
        final var response = template.exchange(request, BoardGetResponse.class);

        assertThat(response.getHeaders().get("Set-Cookie")).isNotNull();
        assertThat(response.getHeaders().get("Set-Cookie").get(0)).startsWith("bbv=1");
    }

    @Test
    public void canSetCookie() {
        // first
        final var firstRequestHeaders = buildDefaultHeaders();
        firstRequestHeaders.add("Cookie", "bbv=1");
        final var firstRequest = buildGetRequest(firstRequestHeaders, "/api/v1/board/2");
        final var firstResponse = template.exchange(firstRequest, BoardGetResponse.class);

        // Parse cookies in response headers
        // Prepare to place the cookie value in the next request header.
        final var firstResponseCookies = firstResponse.getHeaders().get("Set-Cookie");
        assertThat(firstResponseCookies).isNotNull();
        final var cookiesToSend = getCookieListFromResponse(firstResponseCookies);

        // second
        final var secondRequestHeaders = buildDefaultHeaders();
        secondRequestHeaders.addAll("Cookie", cookiesToSend);
        final var secondRequest = buildGetRequest(secondRequestHeaders, "/api/v1/board/3");
        final var secondResponse = template.exchange(secondRequest, BoardGetResponse.class);
        final var secondResponseCookies = secondResponse.getHeaders().get("Set-Cookie");

        assertThat(secondResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(secondResponseCookies).isNotNull();
        assertThat(secondResponseCookies.get(0)).startsWith("bbv=1/2/3");
    }

    private static ArrayList<String> getCookieListFromResponse(List<String> cookiesFromResponse) {
        final var cookiesToSend = new ArrayList<String>();
        for (String cookie : cookiesFromResponse) {
            String[] cookiePairs = cookie.split(";");
            cookiesToSend.add(cookiePairs[0]);
        }
        return cookiesToSend;
    }
}
