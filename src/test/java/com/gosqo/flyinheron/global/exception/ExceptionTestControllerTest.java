package com.gosqo.flyinheron.global.exception;

import com.gosqo.flyinheron.controller.WebMvcTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

class ExceptionTestControllerTest extends WebMvcTestBase {

    public ExceptionTestControllerTest(MockMvc mockMvc) {
        super(mockMvc);
    }

    @Test
    void throwBadCredentialsException() {
    }

    @Test
    void throwExpireJwtException() {
    }

    @Test
    void throwAuthRequired() {
    }

    @Test
    void throwNoResourceFoundException() {
    }

    @Test
    void throwGetNullPointerException() {
    }

    @Test
    void throwPostNullPointerException() {
    }
}