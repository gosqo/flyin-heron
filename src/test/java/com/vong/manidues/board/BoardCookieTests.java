package com.vong.manidues.board;

import com.vong.manidues.utility.mvc.MvcUtility;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureWebMvc
@Slf4j
public class BoardCookieTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MvcUtility mvcUtil;

    @Test
    public void canSetCookie() throws Exception {
        // first
        String firstUri = "/api/v1/board/2";
        Cookie firstRequestCookie = new Cookie("bbv", "1");

        MvcResult firstRequestResult = mockMvc.perform(
                        get(firstUri)
                                .cookie(firstRequestCookie)
                                .headers(MvcUtility.DEFAULT_HEADER)
                )
                .andExpectAll(
                        status().isOk()
                        , cookie().value("bbv", "1/2"))
                .andDo(print())
                .andReturn();

        mvcUtil.logResultHeaders(firstRequestResult);

        // second
        Cookie secondRequestCookie = firstRequestResult.getResponse().getCookie("bbv");
        String secondUri = "/api/v1/board/3";

        MvcResult secondRequestResult = mockMvc.perform(
                        get(secondUri)
                                .headers(MvcUtility.DEFAULT_HEADER)
                                .cookie(secondRequestCookie)
                )
                .andExpectAll(
                        status().isOk()
                        , cookie().value("bbv", "1/2/3"))
                .andDo(print())
                .andReturn();

        mvcUtil.logResultHeaders(secondRequestResult);
    }


}
