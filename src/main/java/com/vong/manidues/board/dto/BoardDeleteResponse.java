package com.vong.manidues.board.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BoardDeleteResponse {
    private boolean isDeleted;
    private String message;
}
