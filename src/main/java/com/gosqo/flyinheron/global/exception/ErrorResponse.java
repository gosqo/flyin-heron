package com.gosqo.flyinheron.global.exception;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ErrorResponse {
    private int status;
    private String message;
}
