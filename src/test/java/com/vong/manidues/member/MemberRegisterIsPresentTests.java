package com.vong.manidues.member;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vong.manidues.member.dto.IsPresentEmailRequest;
import com.vong.manidues.member.dto.IsPresentNicknameRequest;
import com.vong.manidues.web.MvcUtility;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class MemberRegisterIsPresentTests {

    private final MockMvc mockMvc;

    @Autowired
    public MemberRegisterIsPresentTests(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    private static String getNicknameRequestBodyAsString(String nickname)
            throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(new IsPresentNicknameRequest(nickname));
    }

    private static String getEmailRequestBodyAsString(String email)
            throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(new IsPresentEmailRequest(email));
    }

    // === tests for nickname validation ===
    @Test
    public void nullNickname() throws Exception {
        String requestBody = getNicknameRequestBodyAsString(null);

        mockMvc.perform(
                        post("/api/v1/member/isPresentNickname")
                                .headers(MvcUtility.DEFAULT_POST_HEADER)
                                .content(requestBody)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void blankNickname() throws Exception {
        String requestBody = getNicknameRequestBodyAsString("");

        mockMvc.perform(
                        post("/api/v1/member/isPresentNickname")
                                .headers(MvcUtility.DEFAULT_POST_HEADER)
                                .content(requestBody)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }
    @Test
    public void isPresentNicknameWithAppropriateNicknames() throws Exception {
        String[] appropriateNicknames = {
                "helloWorld"
                , "가나다라"
                , "0908"
                , "hello월드"
                , "hello월드09"
                , "월드099"
        };

        for (String nickname : appropriateNicknames) {
            String requestBody = getNicknameRequestBodyAsString(nickname);

            mockMvc.perform(
                            post("/api/v1/member/isPresentNickname")
                                    .headers(MvcUtility.DEFAULT_POST_HEADER)
                                    .content(requestBody)
                    )
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andDo(MockMvcResultHandlers.print());
        }
    }

    @Test
    public void isPresentNicknameWithInappropriateNickname() throws Exception {
        String[] inappropriateNicknames = {
                "helloWorldHelloWorldHelloWorld" // 20자 초과
                , "h" // 2 자리 미만
                , "helloWorld@@" // 비허용 특수문자 포함
                , "hello world" // 공백 포함 (비허용 특수문자)
        };

        for (String nickname : inappropriateNicknames) {
            String requestBody = getNicknameRequestBodyAsString(nickname);

            mockMvc.perform(
                            post("/api/v1/member/isPresentNickname")
                                    .headers(MvcUtility.DEFAULT_POST_HEADER)
                                    .content(requestBody)
                    )
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andDo(MockMvcResultHandlers.print());
        }
    }

    // === tests for email validation ===
    @Test
    public void nullEmail() throws Exception {
        String requestBody = getEmailRequestBodyAsString(null);

        mockMvc.perform(
                        post("/api/v1/member/isPresentEmail")
                                .headers(MvcUtility.DEFAULT_POST_HEADER)
                                .content(requestBody)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void blankEmail() throws Exception {
        String requestBody = getEmailRequestBodyAsString("");

        mockMvc.perform(
                        post("/api/v1/member/isPresentEmail")
                                .headers(MvcUtility.DEFAULT_POST_HEADER)
                                .content(requestBody)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void isPresentEmailWithAppropriateEmails() throws Exception {
        String[] appropriateEmails = {
                "helllo@world.cc"
                , "hello@world00.com"
                , "hello00@world00.com"
                , "hello.00-asd_dasd@world.00-asd_asd9.com"
                , "hello@world0-asd_asd.asd0.com"
                , "hello@world.d09.co"
                , "hello@world-asd_asd.d09.cac"
        };

        for (String email : appropriateEmails) {
            String requestBody = getEmailRequestBodyAsString(email);

            mockMvc.perform(
                            post("/api/v1/member/isPresentEmail")
                                    .headers(MvcUtility.DEFAULT_POST_HEADER)
                                    .content(requestBody)
            )
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andDo(MockMvcResultHandlers.print());

        }
    }

    @Test
    public void isPresentEmailWithInappropriateEmail() throws Exception {
        String[] inappropriateEmails = {
                "helllo@world..cc" // 마지막 . 이전에 영문 대소문자 혹은 숫자가 존재하지 않음.
                , "hello@world00.commm" // 최상위 도메인 자리수 초과
                , "hello@world.d09.c" // 최상위 도메인 자리수 미달
                , "hello@world--asd_asd.d09.co" // 연속된 하이픈
                , "hello@world-_._..-asd_asd.d09.com" // 허용 특수문자의 연속된 사용
                , "hello@world*asd.com" // 부적절 특수문자 포함
        };

        for (String email : inappropriateEmails) {
            String requestBody = getEmailRequestBodyAsString(email);

            mockMvc.perform(
                    post("/api/v1/member/isPresentEmail")
                            .headers(MvcUtility.DEFAULT_POST_HEADER)
                            .content(requestBody)
            )
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andDo(MockMvcResultHandlers.print());
        }
    }
}
