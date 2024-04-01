package com.vong.manidues.board.dto;

import com.vong.manidues.board.Board;
import com.vong.manidues.member.Member;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardRegisterRequest {

    private Member member;

    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

    public Board toEntity(Member member) {
        return Board.builder()
                .member(member)
                .title(this.title)
                .content(this.content)
                .build();
    }

}
