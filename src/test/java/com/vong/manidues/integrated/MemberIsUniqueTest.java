package com.vong.manidues.integrated;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vong.manidues.domain.fixture.MemberFixture;
import com.vong.manidues.dto.member.IsUniqueEmailRequest;
import com.vong.manidues.dto.member.IsUniqueNicknameRequest;
import com.vong.manidues.repository.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@AutoConfigureMockMvc
class MemberIsUniqueTest extends SpringBootTestBase {
    private final MockMvc mockMvc;
    private final MemberRepository memberRepository;

    @Autowired
    public MemberIsUniqueTest(TestRestTemplate template, MockMvc mockMvc, MemberRepository memberRepository) {
        super(template);
        this.mockMvc = mockMvc;
        this.memberRepository = memberRepository;
    }

    private static String getNicknameRequestBodyAsString(String nickname)
            throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(new IsUniqueNicknameRequest(nickname));
    }

    private static String getEmailRequestBodyAsString(String email)
            throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(new IsUniqueEmailRequest(email));
    }

    @Override
    void initData() {
        member = memberRepository.save(buildMember());
    }

    @Override
    @BeforeEach
    void setUp() {
        initData();
    }

    @Override
    @AfterEach
    void tearDown() {
        memberRepository.deleteAll();
    }

    @Nested
    class If_Email {

        @Test
        public void not_exist_and_is_valid_response_Ok() throws Exception {
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
                                post("/api/v1/member/isUniqueEmail")
                                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                                        .content(requestBody)
                        )
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print());
            }
        }

        @Test
        public void exists_response_Conflict() throws Exception {
            String requestBody = getEmailRequestBodyAsString(MemberFixture.EMAIL);

            mockMvc.perform(
                            post("/api/v1/member/isUniqueEmail")
                                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                                    .content(requestBody)
                    )
                    .andExpect(MockMvcResultMatchers.status().isConflict())
                    .andDo(MockMvcResultHandlers.print());
        }

        @Nested
        class Is_not_valid_like_cases_below_response_Bad_Request {

            @Test
            public void null_() throws Exception {
                String requestBody = getEmailRequestBodyAsString(null);

                mockMvc.perform(
                                post("/api/v1/member/isUniqueEmail")
                                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                                        .content(requestBody)
                        )
                        .andExpect(MockMvcResultMatchers.status().isBadRequest())
                        .andDo(MockMvcResultHandlers.print());
            }

            @Test
            public void blank() throws Exception {
                String requestBody = getEmailRequestBodyAsString("");

                mockMvc.perform(
                                post("/api/v1/member/isUniqueEmail")
                                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                                        .content(requestBody)
                        )
                        .andExpect(MockMvcResultMatchers.status().isBadRequest())
                        .andDo(MockMvcResultHandlers.print());
            }

            @Test
            public void filled_but_not_valid() throws Exception {
                String[] inappropriateEmails = {
                        "hello@world..cc" // 마지막 . 이전에 영문 대소문자 혹은 숫자가 존재하지 않음.
                        , "hello@world00.commm" // 최상위 도메인 자리수 초과
                        , "hello@world.d09.c" // 최상위 도메인 자리수 미달
                        , "hello@world--asd_asd.d09.co" // 연속된 하이픈
                        , "hello@world-_._..-asd_asd.d09.com" // 허용 특수문자의 연속된 사용
                        , "hello@world*asd.com" // 부적절 특수문자 포함
                };

                for (String email : inappropriateEmails) {
                    String requestBody = getEmailRequestBodyAsString(email);

                    mockMvc.perform(
                                    post("/api/v1/member/isUniqueEmail")
                                            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                                            .content(requestBody)
                            )
                            .andExpect(MockMvcResultMatchers.status().isBadRequest())
                            .andDo(MockMvcResultHandlers.print());
                }
            }
        }
    }

    @Nested
    class If_nickname {
        @Test
        public void not_exist_and_is_valid_response_Ok() throws Exception {
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
                                post("/api/v1/member/isUniqueNickname")
                                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                                        .content(requestBody)
                        )
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print());
            }
        }

        @Test
        public void exists_response_Conflict() throws Exception {
            String requestBody = getNicknameRequestBodyAsString(MemberFixture.NICKNAME);

            mockMvc.perform(
                            post("/api/v1/member/isUniqueNickname")
                                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                                    .content(requestBody)
                    )
                    .andExpect(MockMvcResultMatchers.status().isConflict())
                    .andDo(MockMvcResultHandlers.print());
        }

        @Nested
        class Is_not_valid_like_cases_below_response_Bad_Request {

            @Test
            public void null_() throws Exception {
                String requestBody = getNicknameRequestBodyAsString(null);

                mockMvc.perform(
                                post("/api/v1/member/isUniqueNickname")
                                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                                        .content(requestBody)
                        )
                        .andExpect(MockMvcResultMatchers.status().isBadRequest())
                        .andDo(MockMvcResultHandlers.print());
            }

            @Test
            public void blank() throws Exception {
                String requestBody = getNicknameRequestBodyAsString("");

                mockMvc.perform(
                                post("/api/v1/member/isUniqueNickname")
                                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                                        .content(requestBody)
                        )
                        .andExpect(MockMvcResultMatchers.status().isBadRequest())
                        .andDo(MockMvcResultHandlers.print());
            }

            @Test
            public void filled_but_not_valid() throws Exception {
                String[] inappropriateNicknames = {
                        "helloWorldHelloWorldHelloWorld" // 20자 초과
                        , "h" // 2 자리 미만
                        , "helloWorld@@" // 비허용 특수문자 포함
                        , "hello world" // 공백 포함 (비허용 특수문자)
                };

                for (String nickname : inappropriateNicknames) {
                    String requestBody = getNicknameRequestBodyAsString(nickname);

                    mockMvc.perform(
                                    post("/api/v1/member/isUniqueNickname")
                                            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                                            .content(requestBody)
                            )
                            .andExpect(MockMvcResultMatchers.status().isBadRequest())
                            .andDo(MockMvcResultHandlers.print());
                }
            }
        }
    }
}
