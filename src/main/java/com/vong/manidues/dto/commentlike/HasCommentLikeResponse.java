package com.vong.manidues.dto.commentlike;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HasCommentLikeResponse {
    private Integer status;
    private String message;

    private Boolean hasLike;
}
