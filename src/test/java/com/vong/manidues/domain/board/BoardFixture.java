package com.vong.manidues.domain.board;

import com.vong.manidues.domain.auth.AuthenticationFixture;
import com.vong.manidues.domain.board.Board;

public class BoardFixture {
    public static final String TITLE = "Hello, Board";
    public static final String CONTENT = "Board's content. Long, long, long";
    public static final Board BOARD_ENTITY = Board.builder()
            .member(AuthenticationFixture.MEMBER_ENTITY)
            .title(TITLE)
            .content(CONTENT)
            .build();
}
