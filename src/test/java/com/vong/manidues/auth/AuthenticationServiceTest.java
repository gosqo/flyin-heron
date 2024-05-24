package com.vong.manidues.auth;

import com.vong.manidues.member.Member;
import com.vong.manidues.member.MemberRepository;
import com.vong.manidues.token.JwtService;
import com.vong.manidues.token.TokenRepository;
import com.vong.manidues.utility.AuthHeaderUtility;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;

import static com.vong.manidues.auth.AuthenticationFixture.MEMBER_EMAIL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DataJpaTest
@ActiveProfiles("test")
class AuthenticationServiceTest {
    private final AuthenticationRequest authRequest = AuthenticationFixture.AUTH_REQUEST;
    private AuthenticationService authService;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authManager;
    @Mock
    private AuthHeaderUtility authHeaderUtility;
    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;

    @Autowired
    public AuthenticationServiceTest(MemberRepository memberRepository, TokenRepository tokenRepository) {
        this.memberRepository = memberRepository;
        this.tokenRepository = tokenRepository;
    }

    @BeforeEach
    void setUp() {
        authService = new AuthenticationService(
                memberRepository, tokenRepository, jwtService, authManager, authHeaderUtility
        );
    }

    @Test
    void authenticate() {
        // given
        Authentication expectedAuth = mock(Authentication.class);
        when(authManager.authenticate(any(Authentication.class)))
                .thenReturn(expectedAuth);
        when(jwtService.generateAccessToken(any(Member.class)))
                .thenReturn("accessToken");
        when(jwtService.generateRefreshToken(any(Member.class)))
                .thenReturn("refreshToken");

        // when
        AuthenticationResponse response = authService.authenticate(authRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isNotNull();
        assertThat(response.getRefreshToken()).isNotNull();
    }

    // mocking 으로 테스트하기 어려운 경우.
    @Test
    void refreshTokenThrowsWithMockToken() {
        // given
        when(authHeaderUtility.extractJwtFromHeader(any(HttpServletRequest.class)))
                .thenReturn("formerRefreshToken");
        when(jwtService.extractUserEmail(any(String.class))).thenReturn(MEMBER_EMAIL);
        when(jwtService.generateRefreshToken(any(Member.class)))
                .thenReturn("reIssuedRefreshToken");

        // when - then
        assertThatThrownBy(() -> {
            authService.refreshToken(mock(HttpServletRequest.class));
        });
    }
}