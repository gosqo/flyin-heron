package com.vong.manidues.domain.board.dto;

import com.vong.manidues.domain.board.Board;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BoardRequestBody {
    @NotBlank(message = "제목을 입력해주세요.")
    protected String title;
    @NotBlank(message = "내용을 입력해주세요.")
    protected String content;

    public BoardRequestBody(Board board) {
        this.title = board.getTitle();
        this.content = board.getContent();
    }
}
