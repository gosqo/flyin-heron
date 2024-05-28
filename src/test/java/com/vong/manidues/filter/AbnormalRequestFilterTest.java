package com.vong.manidues.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vong.manidues.auth.AuthenticationController;
import com.vong.manidues.auth.AuthenticationService;
import com.vong.manidues.board.BoardController;
import com.vong.manidues.board.BoardPageController;
import com.vong.manidues.board.BoardService;
import com.vong.manidues.common.ViewController;
import com.vong.manidues.config.SecurityConfig;
import com.vong.manidues.exception.ExceptionTestController;
import com.vong.manidues.member.MemberController;
import com.vong.manidues.member.MemberRepository;
import com.vong.manidues.member.MemberService;
import com.vong.manidues.member.MemberViewController;
import com.vong.manidues.token.JwtService;
import com.vong.manidues.token.TokenExceptionTestController;
import com.vong.manidues.utility.AuthHeaderUtility;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.validation.Validator;

import static com.vong.manidues.auth.AuthenticationFixture.AUTH_REQUEST;
import static com.vong.manidues.auth.AuthenticationFixture.MEMBER_REGISTER_REQUEST;
import static com.vong.manidues.web.HttpUtility.DEFAULT_GET_HEADERS;
import static com.vong.manidues.web.HttpUtility.DEFAULT_POST_HEADERS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = {
                ViewController.class
                , AuthenticationController.class
                , BoardController.class
                , BoardPageController.class
                , MemberController.class
                , MemberViewController.class
                , ExceptionTestController.class
                , TokenExceptionTestController.class
        }
)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
@Slf4j
public class AbnormalRequestFilterTest {
    private final MockMvc mockMvc;

    @Autowired
    public AbnormalRequestFilterTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @MockBean
    private Validator validator;
    @MockBean
    private MemberService service;
    @MockBean
    private MemberRepository repository;
    @MockBean
    private BoardService boardService;
    @MockBean
    private AuthHeaderUtility authHeaderUtility;
    @MockBean
    private AuthenticationService authService;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private AuthenticationProvider authProvider;
    @MockBean
    private LogoutHandler logoutHandler;

    @BeforeEach
    void setUp() throws Exception {
        when(authHeaderUtility.isNotAuthenticated(any(HttpServletRequest.class)))
                .thenReturn(true);
    }

    @Test
    public void requestPostToRegisteredURI() throws Exception {
        final ObjectMapper objMapper = new ObjectMapper();

        for (String uri : SecurityConfig.WHITE_LIST_URIS_NON_MEMBER_POST) {
            var request = buildMockPostRequest(uri);

            if (uri.endsWith("**")) {
                continue;
            }

            if (uri.equals("/error")) {
                mockMvc.perform(request)
                        .andExpect(status().isInternalServerError());
                continue;
            }

            if (uri.equals("/api/v1/auth/authenticate")) {
                request.content(objMapper.writeValueAsString(AUTH_REQUEST));

                mockMvc.perform(request)
                        .andExpect(status().isOk());
                continue;
            }

            if (uri.equals("api/v1/member")) {
                request.content(objMapper.writeValueAsString(MEMBER_REGISTER_REQUEST));

                mockMvc.perform(request)
                        .andExpect(status().isOk());
            }
        }
    }

    @Test
    public void requestPostToRegisteredResourceExactlyMatches() throws Exception {
        performPost("/error")
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void requestGetToRegisteredURI() throws Exception {
        for (String uri : SecurityConfig.WHITE_LIST_URIS_NON_MEMBER_GET) {
            if (uri.equals("/error") || uri.equals("/api/v1/exception")) {
                performGet(uri)
                        .andExpect(status().isInternalServerError());
                continue;
            }
            if (uri.endsWith("**") || uri.equals("/h2-console")) {
                continue;
            }
            performGet(uri)
                    .andExpect(status().isOk());
        }
    }

    @Test
    public void requestGetToRegisteredResourceExactlyMatches() throws Exception {
        performGet("/error").andExpect(status().isInternalServerError());
    }

    @Test
    public void requestToRegisteredResourceMatchesTest() throws Exception {
        performGet("/errorasd").andExpect(status().isForbidden());
    }

    // controller 중심의 테스트로,
    // 요청의 매개변수를 읽고, 응답에 문제가 없으므로 andExpect(status().isOk())
    @Test
    public void requestToRegisteredResourceUnderSlash() throws Exception {
        performGet("/api/v1/board/9999").andExpect(status().isOk());
    }

    @Test
    public void unregisteredResourceRequest() throws Exception {
        performGet("/unregistered.").andExpect(status().isForbidden());
    }

    @Test
    public void abnormalOrNullUserAgentRequest() throws Exception {
        var request = request(HttpMethod.GET, "/")
                .header("Connection", "keep-alive");

        mockMvc.perform(request)
                .andExpect(status().isForbidden());
    }

    @Test
    public void abnormalOrNullConnectionHeaderRequest() throws Exception {
        var request = request(HttpMethod.GET, "/")
                .header("User-Agent", "Mozilla");

        mockMvc.perform(request)
                .andExpect(status().isForbidden());
    }

    private static MockHttpServletRequestBuilder buildMockPostRequest(String uri) {
        return request(HttpMethod.POST, uri) // HTTP method POST
                .headers(DEFAULT_POST_HEADERS)
                .contentType(MediaType.APPLICATION_JSON);
    }

    private static MockHttpServletRequestBuilder buildMockGetRequest(String uri) {
        return request(HttpMethod.GET, uri) // HTTP method POST
                .headers(DEFAULT_GET_HEADERS);
    }

    private ResultActions performGet(String uri) throws Exception {
        return mockMvc.perform(buildMockGetRequest(uri));
    }

    private ResultActions performPost(String uri) throws Exception {
        return mockMvc.perform(buildMockPostRequest(uri));
    }
}
