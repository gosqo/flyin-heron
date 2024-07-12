package com.vong.manidues.domain.board.dto;

import com.vong.manidues.domain.board.Board;
import com.vong.manidues.domain.member.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class BoardRegisterRequest extends BoardRequestBody {
    public Board toEntity(Member member) {
        return Board.builder()
                .member(member)
                .title(super.title)
                .content(super.content)
                .build();
    }
}
