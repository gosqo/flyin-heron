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
        private static final int PAGE_SIZE = 6;
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
        void getCommentsLikedBy() {
            final int pageNumber = 1;
            commentService.getCommentsLikedBy(member.getEmail(), pageNumber);

            /*select
            cl1_0.id,
                    cl1_0.comment_id,
                    cl1_0.content_modified_at,
                    cl1_0.deleted_at,
                    cl1_0.member_id,
                    cl1_0.registered_at,
                    cl1_0.status,
                    cl1_0.updated_at
            from
                    comment_like cl1_0
            join
                    member m1_0
            on m1_0.id=cl1_0.member_id
            where
                    m1_0.id=?
            order by
                    cl1_0.id
            offset
                    ? rows
            fetch
                    first ? rows only*/
        }

        @Test
        @DisplayName("Request page is empty, then throws")
        void emptySliceThrows() {
            Long boardId = boards.get(0).getId();
            int pageCount = COMMENT_COUNT / PAGE_SIZE;
            // 1 for throwing exception, 1 for getPageRequest() which index begin from 0.
            int requestPage = (pageCount + 1 + 1);

            assertThatThrownBy(() -> commentService.getCommentSliceOf(boardId, requestPage));
        }
    }
}
