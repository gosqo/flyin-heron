package com.gosqo.flyinheron.dto.commentlike;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HasCommentLikeResponse {
    private Integer status;
    private boolean hasLike;
}
