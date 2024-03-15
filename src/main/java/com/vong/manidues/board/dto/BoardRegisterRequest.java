package com.vong.manidues.board.dto;

import com.vong.manidues.board.Board;
import com.vong.manidues.member.Member;
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
    private String title;
    private String content;

    public Board toEntity(Member member) {
        return Board.builder()
                .member(member)
                .title(this.title)
                .content(this.content)
                .build();
    }

}
