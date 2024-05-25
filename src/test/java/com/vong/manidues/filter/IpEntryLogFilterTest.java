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
public class IpEntryLogFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void normalRequest() throws Exception {
        MockHttpServletRequestBuilder request =
                MockMvcRequestBuilders
                        .request(HttpMethod.GET, "/")
                        .header("User-Agent", "Mozilla")
                        .header("Connection", "keep-alive");

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void requestFavicon() throws Exception {
        MockHttpServletRequestBuilder request =
                MockMvcRequestBuilders
                        .request(HttpMethod.GET, "/favicon.ico");

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
