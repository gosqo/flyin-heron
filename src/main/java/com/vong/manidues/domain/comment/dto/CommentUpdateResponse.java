package com.vong.manidues.domain.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentUpdateResponse {
    private int status;
    private String message;
    private String updatedContent;
}
