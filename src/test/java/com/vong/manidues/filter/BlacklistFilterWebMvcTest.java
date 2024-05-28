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
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static com.vong.manidues.web.MvcUtility.buildMockGetRequest;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        var request = buildMockGetRequest("/").remoteAddress("127.0.0.2");

        for (int i = 0; i < 74; i++) {
            log.info("{}", i);
            if (i >= 70) {
                mockMvc.perform(request).andExpect(status().isForbidden());
                continue;
            }
            mockMvc.perform(request).andExpect(status().isOk());
        }

        var request2 = buildMockGetRequest("/").remoteAddress("127.0.0.3");

        for (int i = 0; i < 71; i++) {
            log.info("{}", i);
            if (i == 70) {
                mockMvc.perform(request2).andExpect(status().isForbidden());
                continue;
            }
            mockMvc.perform(request2).andExpect(status().isOk());
        }
    }
}
