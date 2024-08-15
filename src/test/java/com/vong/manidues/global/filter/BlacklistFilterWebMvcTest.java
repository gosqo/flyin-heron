package com.vong.manidues.global.filter;

import com.vong.manidues.controller.ViewController;
import com.vong.manidues.controller.WebMvcTestBase;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ViewController.class)
@Slf4j
class BlacklistFilterWebMvcTest extends WebMvcTestBase {
    @Autowired
    BlacklistFilterWebMvcTest(MockMvc mockMvc) {
        super(mockMvc);
    }

    @Test
    void get_blacklisted_when_request_over_70_times_within_10_seconds() throws Exception {
        var request = request(HttpMethod.GET, "/").remoteAddress("127.0.0.2");

        for (int i = 0; i < 74; i++) {
            log.info("{}", i);
            if (i >= 70) {
                mockMvc.perform(request).andExpect(status().isNotFound());
                continue;
            }
            mockMvc.perform(request).andExpect(status().isOk());
        }

        var request2 = request(HttpMethod.GET, "/").remoteAddress("127.0.0.3");

        for (int i = 0; i < 71; i++) {
            log.info("{}", i);
            if (i == 70) {
                mockMvc.perform(request2).andExpect(status().isNotFound());
                continue;
            }
            mockMvc.perform(request2).andExpect(status().isOk());
        }
    }
}
