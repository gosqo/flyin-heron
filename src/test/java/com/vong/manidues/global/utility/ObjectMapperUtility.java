package com.vong.manidues.global.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.vong.manidues.dto.CustomSliceImpl;
import org.springframework.data.domain.Slice;

public class ObjectMapperUtility {

    public static void addCustomSliceImplToObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addAbstractTypeMapping(Slice.class, CustomSliceImpl.class); // 추상화된 인터페이스를 구체 클래스로 매핑함을 지시.
        objectMapper.registerModule(module);
    }
}
