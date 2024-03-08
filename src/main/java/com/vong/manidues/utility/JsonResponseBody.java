package com.vong.manidues.utility;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JsonResponseBody {

    private int statusCode;
    private String message;
    private String additionalMessage;
}
