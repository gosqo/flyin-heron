package com.vong.manidues.global.utility;

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
