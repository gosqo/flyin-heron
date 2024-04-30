package com.vong.manidues.filter;

import com.vong.manidues.config.SecurityConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class AbnormalRequestFilterTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void requestPostToRegisteredURI() throws Exception {
        for (String uri : SecurityConfig.WHITE_LIST_URIS_NON_MEMBER_POST) {
            MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                    .request(HttpMethod.POST, uri) // HTTP method POST
                    .header("User-Agent", "Mozilla")
                    .header("Connection", "keep-alive");

            if (uri.equals("/error")) {
                mockMvc.perform(request)
                        .andExpect(MockMvcResultMatchers.status().isInternalServerError());
                continue;
            }
            mockMvc.perform(request)
                    .andExpect(MockMvcResultMatchers.status().isOk());
        }
    }

    @Test
    public void requestGetToRegisteredURI() throws Exception {
        for (String uri : SecurityConfig.WHITE_LIST_URIS_NON_MEMBER_GET) {
            MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                    .request(HttpMethod.GET, uri) // HTTP method POST
                    .header("User-Agent", "Mozilla")
                    .header("Connection", "keep-alive");

            if (uri.equals("/error")) {
                mockMvc.perform(request)
                        .andExpect(MockMvcResultMatchers.status().isInternalServerError());
                continue;
            }
            mockMvc.perform(request)
                    .andExpect(MockMvcResultMatchers.status().isOk());
        }
    }

    @Test
    public void requestPostToRegisteredResourceExactlyMatches() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .request(HttpMethod.POST, "/error") // HTTP method POST
                .header("User-Agent", "Mozilla")
                .header("Connection", "keep-alive");

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    public void requestGetToRegisteredResourceExactlyMatches() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .request(HttpMethod.GET, "/error") // HTTP method GET
                .header("User-Agent", "Mozilla")
                .header("Connection", "keep-alive");

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    public void requestToRegisteredResourceMatchesTest() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .request(HttpMethod.GET, "/errorasd") // starts with registered URI but not matching to it.
                .header("User-Agent", "Mozilla")
                .header("Connection", "keep-alive");

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void requestToRegisteredResourceUnderSlash() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .request(HttpMethod.GET, "/api/v1/board/9999")
                .header("User-Agent", "Mozilla")
                .header("Connection", "keep-alive");

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void unregisteredResourceRequest() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .request(HttpMethod.GET, "/unregistered.")
                .header("User-Agent", "Mozilla")
                .header("Connection", "keep-alive");

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void abnormalOrNullUserAgentRequest() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .request(HttpMethod.GET, "/")
                .header("Connection", "keep-alive");

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void abnormalOrNullConnectionHeaderRequest() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .request(HttpMethod.GET, "/")
                .header("User-Agent", "Mozilla");

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}
