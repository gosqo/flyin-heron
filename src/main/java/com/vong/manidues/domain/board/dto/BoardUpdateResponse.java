package com.vong.manidues.domain.board.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BoardUpdateResponse {
    private Long id;
    private String message;
}
