package com.vong.manidues.board;

import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class BoardCookieTests {
    @Autowired
    MockMvc mvc;

    @Test
    public void canSetCookie() throws Exception {
        MockHttpServletRequestBuilder request0 = MockMvcRequestBuilders
                .request(HttpMethod.GET, "/api/v1/board/1")
                .cookie(new Cookie("BoardBeenViewed", "1"))
                .header("User-Agent", "Mozilla")
                .header("Connection", "keep-alive");

        mvc.perform(request0).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
        mvc.perform(request0).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        MockHttpServletRequestBuilder request1 = MockMvcRequestBuilders
                .request(HttpMethod.GET, "/api/v1/board/2")
                .header("User-Agent", "Mozilla")
                .header("Connection", "keep-alive");

        mvc.perform(request1).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }
}
