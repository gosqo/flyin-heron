package com.vong.manidues.service;

import com.vong.manidues.domain.BaseEntity;
import com.vong.manidues.domain.Comment;
import com.vong.manidues.domain.CommentLike;
import com.vong.manidues.domain.Member;
import com.vong.manidues.dto.commentlike.DeleteCommentLikeResponse;
import com.vong.manidues.dto.commentlike.HasCommentLikeResponse;
import com.vong.manidues.dto.commentlike.RegisterCommentLikeResponse;
import com.vong.manidues.repository.CommentLikeRepository;
import com.vong.manidues.repository.CommentRepository;
import com.vong.manidues.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CommentLikeService {
    private final CommentLikeRepository commentLikeRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;

    public DeleteCommentLikeResponse deleteCommentLike(String memberEmail, Long commentId) {
        Member member = memberRepository.findByEmail(memberEmail).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 회원의 댓글 좋아요 삭제 요청.")
        );
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 댓글에 좋아요 삭제 요청.")
        );
        CommentLike commentLike = commentLikeRepository.findByMemberIdAndCommentId(member.getId(), commentId).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 '댓글 좋아요'에 삭제 요청.")
        );

        commentLike.softDelete();
        comment.subtractLikeCount(); // Comment 엔티티에 좋아요 수 줄임.

        return DeleteCommentLikeResponse.builder()
                .status(200)
                .message("댓글에 좋아요를 취소했습니다.")
                .build();
    }

    public HasCommentLikeResponse hasLike(Long commentId, String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 회원의 댓글 좋아요 여부 조회 요청.")
        );
        commentRepository.findById(commentId).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 댓글에 좋아요 여부 조회 요청.")
        );

        CommentLike commentLike = commentLikeRepository.findByMemberIdAndCommentId(member.getId(), commentId)
                .orElse(null);
        boolean hasLike = commentLike != null;

        return HasCommentLikeResponse.builder()
                .status(200)
                .hasLike(hasLike)
                .build();
    }

    public RegisterCommentLikeResponse registerCommentLike(Long commentId, String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 회원의 댓글 좋아요 요청.")
        );
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 댓글에 좋아요 요청.")
        );
        CommentLike commentLike = CommentLike.builder()
                .member(member)
                .comment(comment)
                .build();

        // 동일 member, comment 의 제약조건으로,
        // DB에 존재하는지 확인 후, 있으면, activate, 없으면 저장.
        commentLikeRepository.findByMemberIdAndCommentId(member.getId(), comment.getId())
                .ifPresentOrElse(
                        BaseEntity::activate
                        , () -> commentLikeRepository.save(commentLike)
                );
        comment.addLikeCount(); // Comment 엔티티에 좋아요 수 늘임.

        return RegisterCommentLikeResponse.builder()
                .status(201)
                .message("댓글에 좋아요를 남겼습니다.")
                .build();
    }
}
