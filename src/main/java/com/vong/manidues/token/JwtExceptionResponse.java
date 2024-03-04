package com.vong.manidues.token;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class JwtExceptionResponse {

    private int statusCode;
    private String exceptionMessage;

    private Map<String, Object> responseMap = new HashMap<>();

    public void putResult(String key, Object value) {}


}
