package com.vong.manidues.domain.comment;

import com.vong.manidues.domain.member.MemberRepository;
import com.vong.manidues.domain.token.ClaimExtractor;
import com.vong.manidues.global.config.ApplicationConfig;
import com.vong.manidues.global.config.SecurityConfig;
import com.vong.manidues.global.utility.AuthHeaderUtility;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
// 앱 컨텍스트에서 가져와 사용할 설정 클래스
// 해당 클래스가 가진 의존성을 스프링을 통해 해결.
// 실제 앱 컨텍스트와 같이 작동.
@Import(value = {
        SecurityConfig.class
        , ApplicationConfig.class
})
// @Import 에 등록한 객체가 의존하는 객체의 의존성 해결을 위한 MockBeans
// 주입받은 의존성 객체가 의존하는 객체를 목으로 처리.
// 동작하는 척 아무일도 하지 않도록 설정하는 클래스들.
@MockBeans({
        @MockBean(CommentService.class)
        , @MockBean(ClaimExtractor.class)
        , @MockBean(AuthHeaderUtility.class)
        , @MockBean(MemberRepository.class)
        , @MockBean(LogoutHandler.class)
})
class CommentControllerTest {
    private final MockMvc mockMvc;

    @Autowired
    public CommentControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Nested
    @DisplayName("Any user can request to ")
    class AnyUserCanRequestTo {

        @Test
        @DisplayName("get a single comment.")
        void getComment() throws Exception {
            mockMvc.perform(get("/api/v1/comment/1"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("get a page of comments.")
        void getPageOfComment() throws Exception {
            mockMvc.perform(get("/api/v1/board/1/comments?page-number=1"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("If user has role 'USER', can request to")
    @WithMockUser(roles = "USER")
    class ProperAuthorizedUserCanRequest {

        @Test
        @DisplayName("register comment.")
        void register() throws Exception {
            mockMvc.perform(post("/api/v1/comment"))
                    .andDo(print())
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("modify comment.")
        void modify() throws Exception {
            mockMvc.perform(put("/api/v1/comment/1"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("remove comment.")
        void remove() throws Exception {
            mockMvc.perform(delete("/api/v1/comment/1"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("If user has no proper authority, cannot request to")
    class NoAuthorizedUserCannot {

        @Test
        @DisplayName("register comment.")
        void register() throws Exception {
            mockMvc.perform(post("/api/v1/comment"))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("modify comment.")
        void modify() throws Exception {
            mockMvc.perform(put("/api/v1/comment/1"))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("remove comment.")
        void remove() throws Exception {
            mockMvc.perform(delete("/api/v1/comment/1"))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }
    }
}