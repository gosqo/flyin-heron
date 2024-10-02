package com.gosqo.flyinheron.service;

import com.gosqo.flyinheron.domain.Comment;
import com.gosqo.flyinheron.domain.CommentLike;
import com.gosqo.flyinheron.domain.EntityStatus;
import com.gosqo.flyinheron.domain.Member;
import com.gosqo.flyinheron.repository.CommentLikeRepository;
import com.gosqo.flyinheron.repository.CommentRepository;
import com.gosqo.flyinheron.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentLikeService {
    private final CommentLikeRepository commentLikeRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public CommentLike deleteCommentLike(Long memberId, Long commentId) {
        Comment comment = commentRepository.findByIdForUpdate(commentId).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 댓글에 좋아요 삭제 요청.")
        );
        CommentLike commentLike = commentLikeRepository.findByMemberIdAndCommentId(memberId, commentId).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 '댓글 좋아요'에 삭제 요청.")
        );

        commentLike.softDelete();
        comment.subtractLikeCount(); // Comment 엔티티에 좋아요 수 줄임.
        log.info("DELETE commentLike done.");

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
        Comment comment = commentRepository.findByIdForUpdate(commentId).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 댓글에 좋아요 요청.")
        );
        AtomicReference<CommentLike> commentLike = new AtomicReference<>();

        commentLikeRepository.findByMemberIdAndCommentId(memberId, comment.getId())
                .ifPresentOrElse(
                        (stored) -> {
                            if (stored.getStatus() != EntityStatus.ACTIVE) {
                                stored.activate();
                                comment.addLikeCount();
                            }
                            commentLike.set(stored);
                        },
                        () -> commentLike.set(commentLikeRepository.save(CommentLike.builder()
                                .member(member)
                                .comment(comment)
                                .build()
                        ))
                );
        log.info("POST commentLike done.");

        return commentLike.get();
    }
}
