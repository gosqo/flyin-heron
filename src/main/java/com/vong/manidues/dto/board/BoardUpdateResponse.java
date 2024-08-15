package com.vong.manidues.dto.board;

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
