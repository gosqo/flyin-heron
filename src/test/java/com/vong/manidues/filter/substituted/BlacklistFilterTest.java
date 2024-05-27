package com.vong.manidues.filter.substituted;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
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
@Slf4j
public class BlacklistFilterTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void checkTrackRequestWith71Requests() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .request(HttpMethod.GET, "/")
                .remoteAddress("127.0.0.6")
                .header("User-Agent", "Mozilla")
                .header("Connection", "keep-alive");

        for (int i = 0; i < 71; i++) {
            log.info("{}", i);
            if (i == 70) mockMvc.perform(request)
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
            else mockMvc.perform(request)
                    .andExpect(MockMvcResultMatchers.status().isOk());
        }

        for (int i = 0; i < 3; i++) {
            mockMvc.perform(request)
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
        }

        MockHttpServletRequestBuilder request2 = MockMvcRequestBuilders
                .request(HttpMethod.GET, "/")
                .remoteAddress("127.0.0.7")
                .header("User-Agent", "Mozilla")
                .header("Connection", "keep-alive");

        for (int i = 0; i < 71; i++) {
            log.info("{}", i);
            if (i == 70) mockMvc.perform(request2)
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
            else mockMvc.perform(request2)
                    .andExpect(MockMvcResultMatchers.status().isOk());
        }
    }
}
