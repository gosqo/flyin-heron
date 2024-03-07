package com.vong.manidues.token;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JwtExceptionResponse {

    private String exceptionMessage;

}
