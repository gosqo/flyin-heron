package com.vong.manidues.domain.board;

import com.vong.manidues.DataJpaTestJpaRepositoryBase;
import com.vong.manidues.domain.comment.CommentRepository;
import com.vong.manidues.domain.member.MemberRepository;
import com.vong.manidues.domain.token.ClaimExtractor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.mock.mockito.SpyBeans;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
class BoardRepositoryTest extends DataJpaTestJpaRepositoryBase {

    @Autowired
    public BoardRepositoryTest(
            MemberRepository memberRepository
            , BoardRepository boardRepository
            , CommentRepository commentRepository
    ) {
        super(memberRepository, boardRepository, commentRepository);
    }

    @Test
    void getPageNormally() {
        Pageable pageable = PageRequest.of(0, BoardServiceImpl.PAGE_SIZE, Sort.Direction.DESC, "id");
        Page<Board> found = boardRepository.findAll(pageable);

        found.getContent().forEach(item -> log.info("{}", item));
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
        ) {
            super(memberRepository, boardRepository, commentRepository);
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


