package com.vong.manidues.domain.member;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vong.manidues.domain.board.BoardRepository;
import com.vong.manidues.domain.comment.CommentRepository;
import com.vong.manidues.domain.integrated.SpringBootTestBase;
import com.vong.manidues.domain.member.dto.IsUniqueEmailRequest;
import com.vong.manidues.domain.member.dto.IsUniqueNicknameRequest;
import com.vong.manidues.domain.token.TokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static com.vong.manidues.web.HttpUtility.DEFAULT_POST_HEADERS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@AutoConfigureMockMvc
public class MemberControllerIsUniqueValidationTest extends SpringBootTestBase {

    private final MockMvc mockMvc;

    @Autowired
    public MemberControllerIsUniqueValidationTest(
            MemberRepository memberRepository,
            TokenRepository tokenRepository,
            BoardRepository boardRepository,
            CommentRepository commentRepository,
            TestRestTemplate template,
            MockMvc mockMvc
    ) {
        super(memberRepository, tokenRepository, boardRepository, commentRepository, template);
        this.mockMvc = mockMvc;
    }

    private static String getNicknameRequestBodyAsString(String nickname)
            throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(new IsUniqueNicknameRequest(nickname));
    }

    private static String getEmailRequestBodyAsString(String email)
            throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(new IsUniqueEmailRequest(email));
    }

    @BeforeEach
    void setUp() {
        initMember();
    }

    // === tests for nickname validation ===
    @Test
    public void nullNickname() throws Exception {
        String requestBody = getNicknameRequestBodyAsString(null);
//        var requestBody = new IsUniqueEmailRequest(null);

        mockMvc.perform(
                        post("/api/v1/member/isUniqueNickname")
                                .headers(DEFAULT_POST_HEADERS)
                                .content(String.valueOf(requestBody))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void blankNickname() throws Exception {
        String requestBody = getNicknameRequestBodyAsString("");

        mockMvc.perform(
                        post("/api/v1/member/isUniqueNickname")
                                .headers(DEFAULT_POST_HEADERS)
                                .content(requestBody)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void isUniqueNicknameWithAppropriateNicknames() throws Exception {
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
                                    .headers(DEFAULT_POST_HEADERS)
                                    .content(requestBody)
                    )
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andDo(MockMvcResultHandlers.print());
        }
    }

    @Test
    public void isUniqueNicknameWithInappropriateNickname() throws Exception {
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
                                    .headers(DEFAULT_POST_HEADERS)
                                    .content(requestBody)
                    )
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andDo(MockMvcResultHandlers.print());
        }
    }

    @Test
    public void isUniqueNicknameWithExistingNickname() throws Exception {
        String requestBody = getNicknameRequestBodyAsString(MemberFixture.NICKNAME);

        mockMvc.perform(
                        post("/api/v1/member/isUniqueNickname")
                                .headers(DEFAULT_POST_HEADERS)
                                .content(requestBody)
                )
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andDo(MockMvcResultHandlers.print());
    }

    // === tests for email validation ===
    @Test
    public void nullEmail() throws Exception {
        String requestBody = getEmailRequestBodyAsString(null);

        mockMvc.perform(
                        post("/api/v1/member/isUniqueEmail")
                                .headers(DEFAULT_POST_HEADERS)
                                .content(requestBody)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void blankEmail() throws Exception {
        String requestBody = getEmailRequestBodyAsString("");

        mockMvc.perform(
                        post("/api/v1/member/isUniqueEmail")
                                .headers(DEFAULT_POST_HEADERS)
                                .content(requestBody)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void isUniqueEmailWithAppropriateEmails() throws Exception {
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
                                    .headers(DEFAULT_POST_HEADERS)
                                    .content(requestBody)
                    )
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andDo(MockMvcResultHandlers.print());
        }
    }

    @Test
    public void isUniqueEmailWithInappropriateEmail() throws Exception {
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
                                    .headers(DEFAULT_POST_HEADERS)
                                    .content(requestBody)
                    )
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andDo(MockMvcResultHandlers.print());
        }
    }

    @Test
    public void isUniqueEmailWithExistingEmail() throws Exception {
        String requestBody = getEmailRequestBodyAsString(MemberFixture.EMAIL);

        mockMvc.perform(
                        post("/api/v1/member/isUniqueEmail")
                                .headers(DEFAULT_POST_HEADERS)
                                .content(requestBody)
                )
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andDo(MockMvcResultHandlers.print());
    }
}
