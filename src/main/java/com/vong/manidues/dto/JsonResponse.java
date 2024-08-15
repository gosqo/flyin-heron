package com.vong.manidues.dto;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class JsonResponse {
    private int status;
    private String message;
}
