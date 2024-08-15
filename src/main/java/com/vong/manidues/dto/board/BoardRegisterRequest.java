package com.vong.manidues.dto.board;

import com.vong.manidues.domain.Board;
import com.vong.manidues.domain.Member;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BoardRegisterRequest {
    @NotBlank(message = "제목을 입력해주세요.")
    protected String title;
    @NotBlank(message = "내용을 입력해주세요.")
    protected String content;

    @Builder
    public BoardRegisterRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public Board toEntity(Member member) {
        return Board.builder()
                .member(member)
                .title(title)
                .content(content)
                .build();
    }
}
