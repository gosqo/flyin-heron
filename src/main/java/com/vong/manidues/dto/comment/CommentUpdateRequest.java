package com.vong.manidues.dto.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class CommentUpdateRequest {
    @NotBlank(message = "내용을 입력해주세요.")
    private String content;
}
