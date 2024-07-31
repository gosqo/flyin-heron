package com.vong.manidues.domain.comment;

import com.vong.manidues.DataJpaTestJpaRepositoryBase;
import com.vong.manidues.domain.board.BoardRepository;
import com.vong.manidues.domain.member.MemberRepository;
import com.vong.manidues.domain.token.ClaimExtractor;
import com.vong.manidues.domain.token.TokenRepository;
import lombok.extern.slf4j.Slf4j;
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
class CommentRepositoryTest extends DataJpaTestJpaRepositoryBase {

    @Autowired
    public CommentRepositoryTest(
            MemberRepository memberRepository
            , BoardRepository boardRepository
            , CommentRepository commentRepository
            , TokenRepository tokenRepository
    ) {
        super(memberRepository, boardRepository, commentRepository, tokenRepository);
    }

    @Test
    void getSliceByBoardId() {
        Long boardId = mainBoardId;
        Pageable pageable = PageRequest.of(0, 4, Sort.Direction.ASC, "id");
        Slice<Comment> found = commentRepository.findByBoardId(boardId, pageable);

        found.getContent().forEach(item -> log.info("{}", item));
    }

    @Nested
    @DisplayName("With Imported Service object")
    @Import({CommentService.class})
    @SpyBeans(@SpyBean(ClaimExtractor.class))
    class WithService extends DataJpaTestJpaRepositoryBase {
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

        @Test
        @DisplayName("Request page is empty, then throws")
        void emptySliceThrows() {
            Long boardId = mainBoardId;
            int pageCount = COMMENT_COUNT / CommentService.PAGE_SIZE;
            // 1 for throwing exception, 1 for getPageRequest() which index begin from 0.
            int requestPage = (pageCount + 1 + 1);

            assertThatThrownBy(() -> commentService.getCommentSliceOf(boardId, requestPage));
        }
    }
}
