package com.vong.manidues.domain.commentlike;

import com.vong.manidues.DataJpaTestJpaRepositoryBase;
import com.vong.manidues.domain.board.BoardRepository;
import com.vong.manidues.domain.comment.CommentRepository;
import com.vong.manidues.domain.member.MemberRepository;
import com.vong.manidues.domain.token.TokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class CommentLikeRepositoryTest extends DataJpaTestJpaRepositoryBase {
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