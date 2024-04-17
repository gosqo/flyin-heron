package com.vong.manidues.member;

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

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class MemberControllerTests {

    @Autowired
    private MockMvc mockMvc;

    // === tests for nickname validation ===
    @Test
    public void testBlankNickname() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .request(HttpMethod.POST, "/api/v1/member/isPresentNickname")
                .header("User-Agent", "Mozilla")
                .header("Connection", "keep-alive")
                .header("Content-Type", "application/json;charset=utf-8")
                .content("{\"nickname\":null}");

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testIsPresentNicknameWithAppropriateNicknames() throws Exception {
        String[] appropriateNicknames = {
                "helloWorld"
                , "가나다라"
                , "0908"
                , "hello월드"
                , "hello월드09"
                , "월드099"
        };

        List<MockHttpServletRequestBuilder> requests = new ArrayList<>();
        for (String nickname : appropriateNicknames) {
            requests.add(MockMvcRequestBuilders
                    .request(HttpMethod.POST, "/api/v1/member/isPresentNickname")
                    .header("User-Agent", "Mozilla")
                    .header("Connection", "keep-alive")
                    .header("Content-Type", "application/json;charset=utf-8")
                    .content(String.format("""
                            {
                                "nickname":"%s"
                            }
                            """, nickname))
            );
        }

        for (MockHttpServletRequestBuilder request : requests) {
            mockMvc.perform(request)
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andDo(MockMvcResultHandlers.print())
            ;
        }
    }

    @Test
    public void testIsPresentNicknameWithInappropriateNickname() throws Exception {
        String[] inappropriateNicknames = {
                "helloWorldHelloWorldHelloWorld" // 20자 초과
                , "h" // 2 자리 미만
                , "helloWorld@@" // 비허용 특수문자 포함
                , "hello world" // 공백 포함 (비허용 특수문자)
        };

        List<MockHttpServletRequestBuilder> requests = new ArrayList<>();
        for (String nickname : inappropriateNicknames) {
            requests.add(MockMvcRequestBuilders
                    .request(HttpMethod.POST, "/api/v1/member/isPresentNickname")
                    .header("User-Agent", "Mozilla")
                    .header("Connection", "keep-alive")
                    .header("Content-Type", "application/json;charset=utf-8")
                    .content(String.format("""
                            {
                                "nickname":"%s"
                            }
                            """, nickname))
            );
        }

        for (MockHttpServletRequestBuilder request : requests) {
            mockMvc.perform(request)
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andDo(MockMvcResultHandlers.print())
            ;
        }
    }

    // === tests for email validation ===
    @Test
    public void testBlankEmail() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .request(HttpMethod.POST, "/api/v1/member/isPresentEmail")
                .header("User-Agent", "Mozilla")
                .header("Connection", "keep-alive")
                .header("Content-Type", "application/json;charset=utf-8")
                .content("{\"email\":null}");

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testIsPresentEmailWithAppropriateEmails() throws Exception {
        String[] appropriateEmails = {
                "helllo@world.cc"
                , "hello@world00.com"
                , "hello00@world00.com"
                , "hello.00-asd_dasd@world.00-asd_asd9.com"
                , "hello@world0-asd_asd.asd0.com"
                , "hello@world.d09.co"
                , "hello@world-asd_asd.d09.cac"
        };

        List<MockHttpServletRequestBuilder> requests = new ArrayList<>();
        for (String email : appropriateEmails) {
            requests.add(MockMvcRequestBuilders
                    .request(HttpMethod.POST, "/api/v1/member/isPresentEmail")
                    .header("User-Agent", "Mozilla")
                    .header("Connection", "keep-alive")
                    .header("Content-Type", "application/json;charset=utf-8")
                    .content(String.format("""
                            {
                                "email":"%s"
                            }
                            """, email))
            );
        }

        for (MockHttpServletRequestBuilder request : requests) {
            mockMvc.perform(request)
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andDo(MockMvcResultHandlers.print())
            ;
        }
    }

    @Test
    public void testIsPresentEmailWithInappropriateEmail() throws Exception {
        String[] inappropriateEmails = {
                "helllo@world..cc" // 마지막 . 이전에 영문 대소문자 혹은 숫자가 존재하지 않음.
                , "hello@world00.commm" // 최상위 도메인 자리수 초과
                , "hello@world.d09.c" // 최상위 도메인 자리수 미달
                , "hello@world--asd_asd.d09.co" // 연속된 하이픈
                , "hello@world-_._..-asd_asd.d09.com" // 허용 특수문자의 연속된 사용
                , "hello@world*asd.com" // 부적절 특수문자 포함
        };

        List<MockHttpServletRequestBuilder> requests = new ArrayList<>();
        for (String email : inappropriateEmails) {
            requests.add(MockMvcRequestBuilders
                    .request(HttpMethod.POST, "/api/v1/member/isPresentEmail")
                    .header("User-Agent", "Mozilla")
                    .header("Connection", "keep-alive")
                    .header("Content-Type", "application/json;charset=utf-8")
                    .content(String.format("""
                            {
                                "email":"%s"
                            }
                            """, email))
            );
        }

        for (MockHttpServletRequestBuilder request : requests) {
            mockMvc.perform(request)
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andDo(MockMvcResultHandlers.print())
            ;
        }
    }
}
