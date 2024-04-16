package com.vong.manidues.member;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
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
public class MemberControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testBlankNickname() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .request(HttpMethod.POST, "/api/v1/member/isPresentNickname")
                .header("User-Agent", "Mozilla")
                .header("Connection", "keep-alive")
                .header("Content-Type", "application/json;charset=utf-8")
                .content("{\"nickname\":null}");

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testBlankEmail() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .request(HttpMethod.POST, "/api/v1/member/isPresentEmail")
                .header("User-Agent", "Mozilla")
                .header("Connection", "keep-alive")
                .header("Content-Type", "application/json;charset=utf-8")
                .content("{\"email\":null}");

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testNotSuitableEmail() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .request(HttpMethod.POST, "/api/v1/member/isPresentEmail")
                .header("User-Agent", "Mozilla")
                .header("Connection", "keep-alive")
                .header("Content-Type", "application/json;charset=utf-8")
                .content("{\"email\":\"hello@world.ddddDDDDD\"}");

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testRegEx() {
        String regExp = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*[0-9a-zA-Z]+.[a-zA-Z]{2,3}$";
        String email = "helllo@world..cc";
        String email2 = "hello@world00.commm";
        String email3 = "helllo@world.d09.cc";
        log.info(String.valueOf(email.matches(regExp)));

        Assertions.assertFalse(email.matches(regExp), "result?");
        Assertions.assertTrue(email2.matches(regExp), "result?");
        Assertions.assertTrue(email3.matches(regExp), "result?");

    }
}
