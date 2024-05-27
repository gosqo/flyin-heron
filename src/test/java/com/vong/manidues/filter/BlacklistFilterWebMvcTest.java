package com.vong.manidues.filter;

import com.vong.manidues.common.ViewController;
import com.vong.manidues.config.SecurityConfig;
import com.vong.manidues.token.JwtService;
import com.vong.manidues.utility.AuthHeaderUtility;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(ViewController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
@Slf4j
public class BlacklistFilterWebMvcTest {
    private final MockMvc mockMvc;

    @Autowired
    public BlacklistFilterWebMvcTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }
    @MockBean
    private AuthenticationProvider authProvider;
    @MockBean
    private LogoutHandler logoutHandler;
    @MockBean
    private AuthHeaderUtility authHeaderUtility;
    @MockBean
    private JwtService jwtService;

    @Test
    public void checkTrackRequestWith71Requests() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .request(HttpMethod.GET, "/")
                .remoteAddress("127.0.0.2")
                .header("User-Agent", "Mozilla")
                .header("Connection", "keep-alive");

        for (int i = 0; i < 71; i++) {
            log.info("{}", i);
            if (i == 70) mockMvc.perform(request)
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
            else mockMvc.perform(request)
                    .andExpect(MockMvcResultMatchers.status().isOk());
        }

        for (int i = 0; i < 3; i++) {
            mockMvc.perform(request)
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
        }

        MockHttpServletRequestBuilder request2 = MockMvcRequestBuilders
                .request(HttpMethod.GET, "/")
                .remoteAddress("127.0.0.3")
                .header("User-Agent", "Mozilla")
                .header("Connection", "keep-alive");

        for (int i = 0; i < 71; i++) {
            log.info("{}", i);
            if (i == 70) mockMvc.perform(request2)
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
            else mockMvc.perform(request2)
                    .andExpect(MockMvcResultMatchers.status().isOk());
        }
    }
}
