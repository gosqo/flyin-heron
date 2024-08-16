package com.vong.manidues.repository;

import com.vong.manidues.domain.Comment;
import com.vong.manidues.service.ClaimExtractor;
import com.vong.manidues.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.mock.mockito.SpyBeans;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
class CommentRepositoryTest extends DataJpaTestRepositoryDataInitializer {

    @Autowired
    public CommentRepositoryTest(
            MemberRepository memberRepository
            , BoardRepository boardRepository
            , CommentRepository commentRepository
            , TokenRepository tokenRepository
    ) {
        super(memberRepository, boardRepository, commentRepository, tokenRepository);
    }

    @BeforeEach
    void setUp() {
        initBoards();
        log.info("==== Test data initialized. ====");
    }

    @Test
    void findByBoardId() {
        Long boardId = boards.get(0).getId();
        Pageable pageable = PageRequest.of(0, 4, Sort.Direction.ASC, "id");
        Slice<Comment> found = commentRepository.findByBoardId(boardId, pageable);

        found.getContent().forEach(item -> log.info("{}", item));
    }

    @Nested
    @DisplayName("With Imported Service object")
    @Import({CommentService.class})
    @SpyBeans(@SpyBean(ClaimExtractor.class))
    class WithService extends DataJpaTestRepositoryDataInitializer {
        private final CommentService commentService;

        @Autowired
        public WithService(
                MemberRepository memberRepository
                , BoardRepository boardRepository
                , CommentRepository commentRepository
                , CommentService commentService
                , TokenRepository tokenRepository
        ) {
            super(memberRepository, boardRepository, commentRepository, tokenRepository);
            this.commentService = commentService;
        }

        @BeforeEach
        void setUp() {
            initBoards();
            log.info("==== Test data initialized. ====");
        }

        @Test
        @DisplayName("Request page is empty, then throws")
        void emptySliceThrows() {
            Long boardId = boards.get(0).getId();
            int pageCount = COMMENT_COUNT / CommentService.PAGE_SIZE;
            // 1 for throwing exception, 1 for getPageRequest() which index begin from 0.
            int requestPage = (pageCount + 1 + 1);

            assertThatThrownBy(() -> commentService.getCommentSliceOf(boardId, requestPage));
        }
    }
}
