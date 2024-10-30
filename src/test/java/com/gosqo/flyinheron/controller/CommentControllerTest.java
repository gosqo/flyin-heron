package com.gosqo.flyinheron.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gosqo.flyinheron.dto.comment.CommentRegisterRequest;
import com.gosqo.flyinheron.dto.comment.CommentUpdateRequest;
import com.gosqo.flyinheron.service.CommentService;
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

@WebMvcTest(CommentController.class)
@MockBean(CommentService.class)
class CommentControllerTest extends WebMvcTestBase {

    @Autowired
    CommentControllerTest(MockMvc mockMvc) {
        super(mockMvc);
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
            CommentRegisterRequest requestBody = CommentRegisterRequest.builder()
                    .boardId(1L)
                    .content("some contents")
                    .build();
            mockMvc.perform(post("/api/v1/comment")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(requestBody)))
                    .andDo(print())
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("modify comment.")
        void modify() throws Exception {
            CommentUpdateRequest requestBody = CommentUpdateRequest.builder()
                    .content("modified content")
                    .build();
            mockMvc.perform(put("/api/v1/comment/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(requestBody)))
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
            CommentRegisterRequest requestBody = CommentRegisterRequest.builder()
                    .boardId(1L)
                    .content("some contents")
                    .build();
            mockMvc.perform(post("/api/v1/comment")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(requestBody)))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("modify comment.")
        void modify() throws Exception {
            CommentUpdateRequest requestBody = CommentUpdateRequest.builder()
                    .content("modified content")
                    .build();
            mockMvc.perform(put("/api/v1/comment/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(requestBody)))
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
