package com.vong.manidues.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

public class CustomSliceImpl<T> extends SliceImpl<T> {

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public CustomSliceImpl(
            @JsonProperty("content") List<T> content
            , @JsonProperty("pageable") JsonNode pageable
            , @JsonProperty("last") boolean last
    ) {
        super(content, PageRequest.of(pageable.get("pageNumber").asInt(), pageable.get("pageSize").asInt()), !last);
    }
}
