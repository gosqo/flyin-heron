package com.vong.manidues.dto.commentlike;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RegisterCommentLikeResponse {
    private int status;
    private String message;

    @Builder
    public RegisterCommentLikeResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
