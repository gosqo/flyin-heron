package com.gosqo.flyinheron.controller;

import com.gosqo.flyinheron.dto.commentlike.DeleteCommentLikeResponse;
import com.gosqo.flyinheron.dto.commentlike.HasCommentLikeResponse;
import com.gosqo.flyinheron.dto.commentlike.RegisterCommentLikeResponse;
import com.gosqo.flyinheron.global.utility.AuthHeaderUtility;
import com.gosqo.flyinheron.service.ClaimExtractor;
import com.gosqo.flyinheron.service.CommentLikeService;
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
        String jwt = AuthHeaderUtility.extractAccessToken(request);
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
        String jwt = AuthHeaderUtility.extractAccessToken(request);
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
        String jwt = AuthHeaderUtility.extractAccessToken(request);
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
