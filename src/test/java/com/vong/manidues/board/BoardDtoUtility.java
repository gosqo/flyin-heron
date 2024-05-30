package com.vong.manidues.board;

import com.vong.manidues.board.dto.BoardRegisterRequest;
import com.vong.manidues.board.dto.BoardUpdateRequest;

public class BoardDtoUtility {
    public static BoardRegisterRequest buildBoardRegisterRequest() {
        return buildBoardRegisterRequest("title", "content");
    }

    public static BoardRegisterRequest buildBoardRegisterRequest(String title) {
        return buildBoardRegisterRequest(title, "content");
    }

    public static BoardRegisterRequest buildBoardRegisterRequest(Board board) {
        return buildBoardRegisterRequest(board.getTitle(), board.getContent());
    }

    public static BoardRegisterRequest buildBoardRegisterRequest(String title, String content) {
        return BoardRegisterRequest.builder()
                .title(title)
                .content(content)
                .build();
    }

    public static BoardUpdateRequest buildBoardUpdateRequest() {
        return buildBoardUpdateRequest("Updated title.", "Updated content.");
    }

    public static BoardUpdateRequest buildBoardUpdateRequest(Board board) {
        return buildBoardUpdateRequest(board.getTitle(), board.getContent());
    }

    public static BoardUpdateRequest buildBoardUpdateRequest(String title, String content) {
        return BoardUpdateRequest.builder()
                .title(title)
                .content(content)
                .build();
    }
}
