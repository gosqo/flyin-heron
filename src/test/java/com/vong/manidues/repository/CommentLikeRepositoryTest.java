package com.vong.manidues.repository;

import com.vong.manidues.domain.CommentLike;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class CommentLikeRepositoryTest extends DataJpaTestRepositoryDataInitializer {
    private final CommentLikeRepository commentLikeRepository;

    @Autowired
    public CommentLikeRepositoryTest(
            MemberRepository memberRepository,
            BoardRepository boardRepository,
            CommentRepository commentRepository,
            TokenRepository tokenRepository,
            CommentLikeRepository commentLikeRepository
    ) {
        super(memberRepository, boardRepository, commentRepository, tokenRepository);
        this.commentLikeRepository = commentLikeRepository;
    }

    @BeforeEach
    void setUp() {
        initComments();
        log.info("==== Test data initialized. ====");
    }

    @Test
    void findByMemberIdAndCommentId() {
        CommentLike commentLike = CommentLike.builder()
                .member(member)
                .comment(comments.get(0))
                .build();

        CommentLike storedCommentLike = commentLikeRepository.save(commentLike);
        log.info(storedCommentLike.toString());
        CommentLike foundCommentLike = commentLikeRepository.findByMemberIdAndCommentId(member.getId(), comments.get(0).getId()).orElseThrow();

        assertThat(storedCommentLike.getId()).isEqualTo(foundCommentLike.getId());
    }
}