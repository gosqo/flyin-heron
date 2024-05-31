package com.vong.manidues.auth;

import com.vong.manidues.member.Member;
import com.vong.manidues.member.MemberRepository;
import com.vong.manidues.token.Token;
import com.vong.manidues.token.TokenRepository;
import com.vong.manidues.token.TokenUtility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;

import static com.vong.manidues.token.TokenUtility.EXPIRATION_7_DAYS;
import static com.vong.manidues.token.TokenUtility.EXPIRATION_8_DAYS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class AuthenticationServiceTest {
    private final AuthenticationService authService;
    private final TokenRepository tokenRepository;
    private final MemberRepository memberRepository;
    private final TokenUtility tokenUtility; // test only Utility.

    @Mock
    private MockHttpServletRequest mockRequest;
    private Member member;

    @Autowired
    public AuthenticationServiceTest(
            AuthenticationService authService
            , MemberRepository memberRepository
            , TokenRepository tokenRepository
            , TokenUtility tokenUtility
    ) {
        this.authService = authService;
        this.memberRepository = memberRepository;
        this.tokenRepository = tokenRepository;
        this.tokenUtility = tokenUtility;
    }

    @BeforeEach
    void setUp() {
        mockRequest = new MockHttpServletRequest();
        member = memberRepository.findById(1L).orElseThrow();
    }

    @AfterEach
    void tearDown() {
        tokenRepository.deleteAll();
    }

    @Test
    void refreshTokenExpirationLeft7Days() {
        // given
        var formerRefreshToken = tokenUtility.buildToken(new HashMap<>(), member, EXPIRATION_7_DAYS);
        saveToken(formerRefreshToken, member);
        mockRequest.addHeader("Authorization", "Bearer " + formerRefreshToken);

        // when
        var response = authService.refreshToken(mockRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getRefreshToken()).isNotEqualTo(formerRefreshToken);
        assertThat(response.getAccessToken()).isNotNull();
    }

    @Test
    void refreshTokenExpirationLeft8Days() {
        // given
        var formerRefreshToken = tokenUtility.buildToken(new HashMap<>(), member, EXPIRATION_8_DAYS);
        saveToken(formerRefreshToken, member);
        mockRequest.addHeader("Authorization", "Bearer " + formerRefreshToken);

        // when
        var response = authService.refreshToken(mockRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getRefreshToken()).isEqualTo(formerRefreshToken);
        assertThat(response.getAccessToken()).isNotNull();
    }

    @Test
    void refreshTokenNotExistOnDatabaseThrows() {
        // given
        var formerRefreshToken = tokenUtility.buildToken(new HashMap<>(), member, EXPIRATION_8_DAYS);
        // no saveToken. formerRefreshToken not exist on Database.
        mockRequest.addHeader("Authorization", "Bearer " + formerRefreshToken);

        // when then
        assertThatThrownBy(() -> authService.refreshToken(mockRequest));
    }

    private void saveToken(String formerRefreshToken, Member member) {
        tokenRepository.save(Token.builder()
                .token(formerRefreshToken)
                .member(member)
                .build()
        );
    }
}