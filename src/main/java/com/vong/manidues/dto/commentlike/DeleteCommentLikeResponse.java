package com.vong.manidues.dto.commentlike;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DeleteCommentLikeResponse {
    private int status;
    private String message;

    @Builder
    public DeleteCommentLikeResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
