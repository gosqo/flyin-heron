package com.vong.manidues.board.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BoardRegisterResponse {

    private Long id;
    private boolean posted;
    private String message;

}
