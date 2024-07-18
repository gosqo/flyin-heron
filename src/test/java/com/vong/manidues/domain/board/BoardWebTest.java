package com.vong.manidues.domain.board;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vong.manidues.domain.board.dto.BoardGetResponse;
import com.vong.manidues.domain.member.Member;
import com.vong.manidues.domain.token.ClaimExtractor;
import com.vong.manidues.domain.token.JwtService;
import com.vong.manidues.global.config.SecurityConfig;
import com.vong.manidues.web.HttpUtility;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static com.vong.manidues.domain.board.BoardUtility.buildMockBoard;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BoardController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
public class BoardWebTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationProvider authProvider;
    @MockBean
    private LogoutHandler logoutHandler;

    @MockBean
    private BoardService service;
    @MockBean
    private BoardRepository repository;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private ClaimExtractor claimExtractor;

    private final ObjectMapper objectMapper;

    @Autowired
    public BoardWebTest(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper.registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @BeforeEach
    void setUp() {
    }

    @Test
    void controllerTest1() throws Exception {
        // given
        var mockBoard = buildMockBoard(1L, mock(Member.class));
        var expectedResponseBody = BoardGetResponse.of(mockBoard);
        var stringifiedResponse = objectMapper.writeValueAsString(expectedResponseBody);
        when(service.get(eq(1L), any(HttpServletRequest.class), any(HttpServletResponse.class)))
                .thenReturn(expectedResponseBody);

        // when, then
        mockMvc.perform(get("/api/v1/board/1")
                        .headers(HttpUtility.DEFAULT_GET_HEADERS))
                .andExpect(status().isOk())
                .andExpect(content().json(stringifiedResponse))
                .andDo(print());
    }
}
