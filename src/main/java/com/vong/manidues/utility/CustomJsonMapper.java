package com.vong.manidues.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomJsonMapper {

    public String mapObjToString(Object obj) throws IOException {
        return new ObjectMapper().writeValueAsString(obj);
    }
}
