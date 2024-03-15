package com.vong.manidues.board.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BoardUpdateResponse {
    private Long id;
    private boolean isUpdated;
}
