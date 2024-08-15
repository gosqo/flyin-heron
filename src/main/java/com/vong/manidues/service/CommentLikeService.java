package com.vong.manidues.service;

import com.vong.manidues.repository.CommentRepository;
import com.vong.manidues.repository.CommentLikeRepository;
import com.vong.manidues.dto.commentlike.HasCommentLikeResponse;
import com.vong.manidues.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentLikeService {
    private final CommentLikeRepository commentLikeRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;

    public HasCommentLikeResponse hasLike(Long memberId, Long commentId) {
        return null;
    }
}
