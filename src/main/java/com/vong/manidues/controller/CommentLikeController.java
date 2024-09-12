package com.vong.manidues.controller;

import com.vong.manidues.dto.commentlike.DeleteCommentLikeResponse;
import com.vong.manidues.dto.commentlike.HasCommentLikeResponse;
import com.vong.manidues.dto.commentlike.RegisterCommentLikeResponse;
import com.vong.manidues.global.utility.AuthHeaderUtility;
import com.vong.manidues.service.ClaimExtractor;
import com.vong.manidues.service.CommentLikeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comment-like")
public class CommentLikeController {

    private final CommentLikeService commentLikeService;
    private final ClaimExtractor claimExtractor;

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/{commentId}")
    public ResponseEntity<HasCommentLikeResponse> hasLike(
            @PathVariable(value = "commentId") Long commentId
            , HttpServletRequest request
    ) {
        String jwt = AuthHeaderUtility.extractJwt(request);
        Long memberId = claimExtractor.extractMemberId(jwt);

        boolean hasLike = commentLikeService.hasLike(memberId, commentId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(HasCommentLikeResponse.builder()
                        .status(200)
                        .hasLike(hasLike)
                        .build());
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/{commentId}")
    public ResponseEntity<RegisterCommentLikeResponse> registerCommentLike(
            @PathVariable(value = "commentId") Long commentId
            , HttpServletRequest request
    ) {
        String jwt = AuthHeaderUtility.extractJwt(request);
        Long memberId = (claimExtractor.extractMemberId(jwt));

        commentLikeService.registerCommentLike(memberId, commentId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(RegisterCommentLikeResponse.builder()
                        .status(201)
                        .message("댓글에 좋아요를 남겼습니다.")
                        .build());
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<DeleteCommentLikeResponse> deleteCommentLike(
            @PathVariable(value = "commentId") Long commentId
            , HttpServletRequest request
    ) {
        String jwt = AuthHeaderUtility.extractJwt(request);
        Long memberId = (claimExtractor.extractMemberId(jwt));

        commentLikeService.deleteCommentLike(memberId, commentId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DeleteCommentLikeResponse.builder()
                        .status(200)
                        .message("댓글에 좋아요를 취소했습니다.")
                        .build());
    }
}
