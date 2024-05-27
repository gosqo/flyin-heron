package com.vong.manidues.board;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vong.manidues.board.dto.BoardGetResponse;
import com.vong.manidues.config.SecurityConfig;
import com.vong.manidues.member.Member;
import com.vong.manidues.token.JwtService;
import com.vong.manidues.utility.AuthHeaderUtility;
import com.vong.manidues.web.HttpUtility;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
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

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BoardController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
@Slf4j
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
    private AuthHeaderUtility authHeaderUtility;
    @MockBean
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void controllerTest1() throws Exception {
        ObjectMapper objMapper = new ObjectMapper()
        // LocalDateTime -> JSON data time array Parsing
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // given
        var board = Board.builder()
                .id(1L)
                .title("title")
                .content("content")
                .member(mock(Member.class))
                .viewCount(0L)
                .registerDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();
        var expectedResponse = new BoardGetResponse().of(board);
        var mappedResponse = objMapper.writeValueAsString(expectedResponse);
        when(service.get(eq(1L), any(HttpServletRequest.class), any(HttpServletResponse.class)))
                .thenReturn(expectedResponse);

        // when, then
        mockMvc.perform(get("/api/v1/board/1")
                        .headers(HttpUtility.DEFAULT_GET_HEADERS))
                .andExpect(status().isOk())
                .andExpect(content().json(mappedResponse))
                .andDo(print());
    }
}
