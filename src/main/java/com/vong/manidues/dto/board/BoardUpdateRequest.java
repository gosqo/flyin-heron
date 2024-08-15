package com.vong.manidues.dto.board;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BoardUpdateRequest {
    @NotBlank(message = "제목을 입력해주세요.")
    private String title;
    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

    @Builder
    public BoardUpdateRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
