package com.vong.manidues.repository;

import com.vong.manidues.service.ClaimExtractor;
import com.vong.manidues.service.BoardServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.mock.mockito.SpyBeans;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
class BoardRepositoryTest extends DataJpaTestJpaRepositoryBase {

    @Autowired
    public BoardRepositoryTest(
            MemberRepository memberRepository
            , BoardRepository boardRepository
            , CommentRepository commentRepository
            , TokenRepository tokenRepository
    ) {
        super(memberRepository, boardRepository, commentRepository, tokenRepository);
    }

    @Nested
    @DisplayName("With Imported Service object")
    @Import({BoardServiceImpl.class})
    @SpyBeans(@SpyBean(ClaimExtractor.class))
    class WithService extends DataJpaTestJpaRepositoryBase {
        private final BoardServiceImpl boardService;

        @Autowired
        public WithService(
                MemberRepository memberRepository
                , BoardRepository boardRepository
                , CommentRepository commentRepository
                , BoardServiceImpl boardService
                , TokenRepository tokenRepository
        ) {
            super(memberRepository, boardRepository, commentRepository, tokenRepository);
            this.boardService = boardService;
        }

        @Test
        @DisplayName("Request page is empty, then throws")
        void emptySliceThrows() {
            // 1 for throwing exception, 1 for getPageRequest makes index begin from 0.
            int requestPage = (BOARD_COUNT / BoardServiceImpl.PAGE_SIZE + 1 + 1);

            assertThatThrownBy(() -> boardService.getBoardPage(requestPage));
        }
    }
}
