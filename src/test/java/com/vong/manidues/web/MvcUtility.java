package com.vong.manidues.web;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.Objects;

@Getter
@Component
@Slf4j
public class MvcUtility {

    public void logResultHeaders(MvcResult requestResult) {
        Arrays.stream(Objects.requireNonNull(requestResult.getRequest().getCookies()))
                .forEach(cookie -> log.info("{}: {}"
                        , cookie.getName()
                        , cookie.getValue()
                ));
    }
}
