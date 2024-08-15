package com.vong.manidues.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vong.manidues.dto.board.BoardRegisterRequest;
import com.vong.manidues.dto.board.BoardUpdateRequest;
import com.vong.manidues.service.BoardServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = {
                BoardController.class
                , BoardPageController.class
        }
)
@MockBean(BoardServiceImpl.class)
class BoardControllerTest extends WebMvcTestBase {

    @Autowired
    BoardControllerTest(MockMvc mockMvc) {
        super(mockMvc);
    }

    @Nested
    @DisplayName("Any user can request to ")
    class AnyUserCanRequestTo {

        @Test
        @DisplayName("get a single board.")
        void getBoard() throws Exception {
            mockMvc.perform(get("/api/v1/board/1"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("get a page of boards.")
        void getPageOfBoard() throws Exception {
            mockMvc.perform(get("/api/v1/boards/1"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("If user has role 'USER', can request to")
    @WithMockUser(roles = "USER")
    class ProperAuthorizedUserCanRequest {

        @Test
        @DisplayName("register board.")
        void register() throws Exception {
            BoardRegisterRequest requestBody = BoardRegisterRequest.builder()
                    .title("some title.")
                    .content("content")
                    .build();
            mockMvc.perform(post("/api/v1/board")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(requestBody)))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("modify board.")
        void modify() throws Exception {
            BoardUpdateRequest requestBody = BoardUpdateRequest.builder()
                    .title("modified title")
                    .content("modified content")
                    .build();
            mockMvc.perform(put("/api/v1/board/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(requestBody)))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("remove board.")
        void remove() throws Exception {
            mockMvc.perform(delete("/api/v1/board/1"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("If user has no proper authority, cannot request to")
    class NoAuthorizedUserCannot {

        @Test
        @DisplayName("register board.")
        void register() throws Exception {
            BoardRegisterRequest requestBody = BoardRegisterRequest.builder()
                    .title("some title.")
                    .content("content")
                    .build();
            mockMvc.perform(post("/api/v1/board")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(requestBody)))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("modify board.")
        void modify() throws Exception {
            BoardUpdateRequest requestBody = BoardUpdateRequest.builder()
                    .title("modified title")
                    .content("modified content")
                    .build();
            mockMvc.perform(put("/api/v1/board/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(requestBody)))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("remove board.")
        void remove() throws Exception {
            mockMvc.perform(delete("/api/v1/board/1"))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }
    }
}
