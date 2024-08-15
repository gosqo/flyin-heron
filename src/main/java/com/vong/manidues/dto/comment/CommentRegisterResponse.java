package com.vong.manidues.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentRegisterResponse {
    private int status;
    private String message;
    private CommentGetResponse comment;
}
