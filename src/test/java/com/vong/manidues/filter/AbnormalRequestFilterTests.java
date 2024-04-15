package com.vong.manidues.filter;

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
public class AbnormalRequestFilterTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void UnregisteredResourceRequest() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .request(HttpMethod.GET, "/unregistered.")
                .header("User-Agent", "Mozilla")
                .header("Connection", "keep-alive");

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void AbnormalOrNullUserAgentRequest() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .request(HttpMethod.GET, "/")
                .header("Connection", "keep-alive");

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void AbnormalOrNullConnectionHeaderRequest() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .request(HttpMethod.GET, "/")
                .header("User-Agent", "Mozilla");

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}
