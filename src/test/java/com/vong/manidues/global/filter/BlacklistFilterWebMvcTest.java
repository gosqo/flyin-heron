package com.vong.manidues.global.filter;

import com.vong.manidues.global.ViewController;
import com.vong.manidues.global.config.SecurityConfig;
import com.vong.manidues.domain.token.ClaimExtractor;
import com.vong.manidues.domain.token.JwtService;
import com.vong.manidues.global.utility.AuthHeaderUtility;
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
    @MockBean
    private ClaimExtractor claimExtractor;

    @Test
    public void checkTrackRequestWith71Requests() throws Exception {
        var request = buildMockGetRequest("/").remoteAddress("127.0.0.2");

        for (int i = 0; i < 74; i++) {
            log.info("{}", i);
            if (i >= 70) {
                mockMvc.perform(request).andExpect(status().isNotFound());
                continue;
            }
            mockMvc.perform(request).andExpect(status().isOk());
        }

        var request2 = buildMockGetRequest("/").remoteAddress("127.0.0.3");

        for (int i = 0; i < 71; i++) {
            log.info("{}", i);
            if (i == 70) {
                mockMvc.perform(request2).andExpect(status().isNotFound());
                continue;
            }
            mockMvc.perform(request2).andExpect(status().isOk());
        }
    }
}
