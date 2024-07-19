package com.vong.manidues.domain.board;

import com.vong.manidues.domain.auth.AuthenticationFixture;

public class BoardFixture {
    public static final Long BOARD_ID = 35L;
    public static final String TITLE = "Hello, Board";
    public static final String CONTENT = "Board's content. Long, long, long";
    public static final Board BOARD_ENTITY = Board.builder()
            .id(BOARD_ID)
            .member(AuthenticationFixture.MEMBER_ENTITY)
            .title(TITLE)
            .content(CONTENT)
            .build();
}
