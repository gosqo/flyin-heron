package com.vong.manidues.wannaknow;

import com.vong.manidues.board.BoardServiceImpl;
import com.vong.manidues.utility.mvc.MvcUtility;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
public class WannaKnowAboutCookieTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MvcUtility mvcUtil;

    @Autowired
    BoardServiceImpl boardService;

    @Test
    public void sendHugeValueCookie() throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/api/v1/board/1")
                                .headers(MvcUtility.DEFAULT_HEADER)
                                .cookie(mvcUtil.findCookieValueLimit())
                )
                .andExpectAll(status().isOk()
                        , cookie().value(
                                "bbv"
                                , boardService
                                .cutFirst500byte(mvcUtil.findCookieValueLimit().getValue())
                                + "/1"
                        )
                )
                .andDo(print())
                .andReturn();

        mvcUtil.logResultHeaders(result);
    }
}
