package com.vong.manidues.service;

import com.vong.manidues.domain.Comment;
import com.vong.manidues.domain.CommentLike;
import com.vong.manidues.domain.Member;
import com.vong.manidues.repository.CommentLikeRepository;
import com.vong.manidues.repository.CommentRepository;
import com.vong.manidues.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CommentLikeService {
    private final CommentLikeRepository commentLikeRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public CommentLike deleteCommentLike(Long memberId, Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 댓글에 좋아요 삭제 요청.")
        );
        CommentLike commentLike = commentLikeRepository.findByMemberIdAndCommentId(memberId, commentId).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 '댓글 좋아요'에 삭제 요청.")
        );

        commentLike.softDelete();
        comment.subtractLikeCount(); // Comment 엔티티에 좋아요 수 줄임.

        return commentLike;
    }

    public boolean hasLike(Long memberId, Long commentId) {
        CommentLike commentLike = commentLikeRepository.findByMemberIdAndCommentId(memberId, commentId).orElse(null);

        return commentLike != null && !commentLike.isSoftDeleted();
    }

    @Transactional
    public CommentLike registerCommentLike(Long memberId, Long commentId) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 회원의 댓글 좋아요 요청.")
        );
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 댓글에 좋아요 요청.")
        );

        CommentLike commentLike = commentLikeRepository.findByMemberIdAndCommentId(memberId, comment.getId())
                .orElse(null);

        comment.addLikeCount(); // Comment 엔티티에 좋아요 수 늘임.

        if (commentLike == null) {
            commentLike = commentLikeRepository.save(CommentLike.builder()
                    .member(member)
                    .comment(comment)
                    .build()
            );

            return commentLike;
        }

        if (!commentLike.isActive()) {
            commentLike.activate();
        }

        return commentLike;
    }
}
