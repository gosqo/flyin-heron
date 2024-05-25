package com.vong.manidues.filter;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
public class BlacklistFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Order(1)
    public void checkTrackRequestWith71Requests() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .request(HttpMethod.GET, "/")
                .header("User-Agent", "Mozilla")
                .header("Connection", "keep-alive");

        for (int i = 0; i < 71; i++) {
            log.info("{}", i);
            if (i == 70) mockMvc.perform(request)
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
            else mockMvc.perform(request)
                    .andExpect(MockMvcResultMatchers.status().isOk());
        }
    }

    @Test
    @Order(2)
    public void afterBlacklistedRequest() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .request(HttpMethod.GET, "/")
                .header("User-Agent", "Mozilla")
                .header("Connection", "keep-alive");

        for (int i = 0; i < 3; i++) {
            mockMvc.perform(request)
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
        }
    }
}
