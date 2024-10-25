package com.gosqo.flyinheron.global.exception;

import com.gosqo.flyinheron.controller.WebMvcTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = {
                ExceptionTestController.class
        }
)
class ExceptionTestControllerTest extends WebMvcTestBase {
    private StringBuilder baseUri;

    @Autowired
    ExceptionTestControllerTest(MockMvc mockMvc) {
        super(mockMvc);
    }

    @BeforeEach
    void setUp() {
        baseUri = new StringBuilder("/api/v1/exception");
    }

    @Test
    void throwBadCredentialsException() throws Exception {
        String uri = baseUri.append("/bad-credentials").toString();
        mockMvc.perform(get(uri))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(containsString("<title>400")));
    }

    @Test
    void throwExpireJwtException() throws Exception {
        String uri = baseUri.append("/expired-jwt").toString();
        mockMvc.perform(get(uri))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(containsString("<title>401")));
    }

    @Test
    void throwAccessDenied() throws Exception {
        String uri = baseUri.append("/access-denied").toString();
        mockMvc.perform(get(uri))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(containsString("<title>403")));
    }

    @Test
    void throwNoResourceFoundException() throws Exception {
        String uri = baseUri.append("/no-resource-found").toString();
        mockMvc.perform(get(uri))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(containsString("<title>404")));
    }

    @Test
    void throwGetNullPointerException() throws Exception {
        String uri = baseUri.append("/null-pointer").toString();
        mockMvc.perform(get(uri))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(containsString("<title>500")));
    }

    @Test
    void throwPostNullPointerException() throws Exception {
        String uri = baseUri.append("/null-pointer").toString();
        mockMvc.perform(post(uri))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{'status':500}"))
                .andExpect(jsonPath("status").value(500));
    }
}