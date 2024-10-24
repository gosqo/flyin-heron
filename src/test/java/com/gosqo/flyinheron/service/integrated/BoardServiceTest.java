package com.gosqo.flyinheron.service.integrated;

import com.gosqo.flyinheron.domain.Board;
import com.gosqo.flyinheron.dto.board.BoardGetResponse;
import com.gosqo.flyinheron.dto.board.BoardPageResponse;
import com.gosqo.flyinheron.global.data.TestDataRemover;
import com.gosqo.flyinheron.repository.BoardRepository;
import com.gosqo.flyinheron.repository.MemberRepository;
import com.gosqo.flyinheron.service.BoardServiceImpl;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BoardServiceTest extends IntegratedServiceTestBase {

    private final BoardServiceImpl service;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;

    @Autowired
    BoardServiceTest(
            TestDataRemover remover
            , BoardServiceImpl service
            , MemberRepository memberRepository
            , BoardRepository boardRepository
    ) {
        super(remover);
        this.service = service;
        this.memberRepository = memberRepository;
        this.boardRepository = boardRepository;
    }

    @BeforeEach
    void setUp() {
        member = memberRepository.save(buildMember());
        boards = boardRepository.saveAll(buildBoards());
    }

    @Nested
    class Get_Page {

        @Test
        void if_page_that_repository_returned_is_empty_Throws_NoResourceFoundException() {
            int NotExistingPage = (boards.size() / BoardServiceImpl.PAGE_SIZE) + 2;

            assertThatThrownBy(() -> service.getBoardPage(NotExistingPage))
                    .isInstanceOf(NoResourceFoundException.class);
        }

        @Test
        void when_looking_for_page() throws NoResourceFoundException {
            int ExistingPage = (boards.size() / BoardServiceImpl.PAGE_SIZE);

            BoardPageResponse returned = service.getBoardPage(ExistingPage);

            assertThat(returned.getBoardPage().getContent()).isNotNull();
        }
    }

    @Nested
    class Get {
        @Test
        void when_board_view_cookie_null_add_Board_viewCount() throws NoResourceFoundException {
            // given
            Board targetBoard = boards.get(0);
            Long formerViewCount = targetBoard.getViewCount();

            // when
            BoardGetResponse returned = service.get(
                    targetBoard.getId()
                    , new MockHttpServletRequest()
                    , new MockHttpServletResponse());

            // then
            assertThat(returned.getViewCount()).isEqualTo(formerViewCount + 1);
        }

        @Test
        void when_board_view_cookie_refers_board_not_affect_viewCount() throws NoResourceFoundException {
            Board targetBoard = boards.get(0);
            Long formerViewCount = targetBoard.getViewCount();

            Cookie cookie = new Cookie(BoardServiceImpl.BOARD_VIEWS_COOKIE_NAME, targetBoard.getId().toString());
            MockHttpServletRequest mockRequest = new MockHttpServletRequest();
            mockRequest.setCookies(cookie);

            // when
            BoardGetResponse returned = service.get(
                    targetBoard.getId()
                    , mockRequest
                    , new MockHttpServletResponse());

            // then
            assertThat(returned.getViewCount()).isEqualTo(formerViewCount);
        }
    }
}
