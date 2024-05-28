package com.vong.manidues.board;

import com.vong.manidues.member.Member;

public class BoardUtility {
    public static Board buildMockBoard(Long id, Member member, Long viewCount) {
        return Board.builder()
                .id(id)
                .title("title")
                .content("content")
                .viewCount(viewCount)
                .member(member)
                .build();
    }
}
