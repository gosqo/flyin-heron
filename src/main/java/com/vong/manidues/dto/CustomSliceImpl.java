package com.vong.manidues.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;

import java.util.ArrayList;
import java.util.List;

public class CustomSliceImpl<T> extends SliceImpl<T> {

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public CustomSliceImpl(
            @JsonProperty("content") List<T> content
            , @JsonProperty("pageable") JsonNode pageable
            , @JsonProperty("first") boolean first
            , @JsonProperty("last") boolean last
            , @JsonProperty("size") int size
            , @JsonProperty("number") int number
            , @JsonProperty("sort") JsonNode sort
            , @JsonProperty("numberOfElements") int numberOfElements
            , @JsonProperty("empty") boolean empty
    ) {
        super(content, PageRequest.of(number, size), !last);
    }

//    public CustomSliceImpl(List<T> content, Pageable pageable, boolean hasNext) {
//        super(content, pageable, hasNext);
//    }

    // 기본 생성자가 있어야 오류 발생 안함.
    public CustomSliceImpl() {
        super(new ArrayList<>());
    }
}
