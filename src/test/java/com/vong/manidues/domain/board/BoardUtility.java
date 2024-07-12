package com.vong.manidues.domain.board;

import com.vong.manidues.domain.board.Board;
import com.vong.manidues.domain.member.Member;

import java.time.LocalDateTime;

public class BoardUtility {
    public static Board buildMockBoard(Long id, Member member, Long viewCount) {
        return Board.builder()
                .id(id)
                .title("title")
                .content("content")
                .viewCount(viewCount)
                .member(member)
                .registerDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();
    }

    public static Board buildMockBoard(Long id, Member member) {
        return Board.builder()
                .id(id)
                .title("title")
                .content("content")
                .viewCount(0L)
                .member(member)
                .registerDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();
    }

    public static Board buildMockBoard(String title, String content) {
        return Board.builder()
                .title(title)
                .content(content)
                .build();
    }

    public static Board buildMockBoard() {
        return Board.builder()
                .title("title")
                .content("content")
                .build();
    }

    public static Board copyBoard(Board board) {
        return Board.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .viewCount(board.getViewCount())
                .registerDate(board.getRegisterDate())
                .updateDate(board.getUpdateDate())
                .member(board.getMember())
                .build();
    }

    public static Board buildBoardAddedViewCount(Board board) {
        var board1 = copyBoard(board);
        board1.addViewCount();
        return board1;
    }
}
