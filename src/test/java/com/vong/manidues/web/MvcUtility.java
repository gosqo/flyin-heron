package com.vong.manidues.web;

import lombok.Getter;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static com.vong.manidues.web.HttpUtility.DEFAULT_GET_HEADERS;
import static com.vong.manidues.web.HttpUtility.DEFAULT_POST_HEADERS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;

@Getter
@Component
public class MvcUtility {
    public static MockHttpServletRequestBuilder buildMockPostRequest(String uri) {
        return request(HttpMethod.POST, uri) // HTTP method POST
                .headers(DEFAULT_POST_HEADERS)
                .contentType(MediaType.APPLICATION_JSON);
    }

    public static MockHttpServletRequestBuilder buildMockGetRequest(String uri) {
        return request(HttpMethod.GET, uri) // HTTP method POST
                .headers(DEFAULT_GET_HEADERS);
    }

    public static ResultActions performGet(String uri, MockMvc mockMvc) throws Exception {
        return mockMvc.perform(buildMockGetRequest(uri));
    }

    public static ResultActions performPost(String uri, MockMvc mockMvc) throws Exception {
        return mockMvc.perform(buildMockPostRequest(uri));
    }
}
