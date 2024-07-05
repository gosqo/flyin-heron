package com.vong.manidues.auth;

import com.vong.manidues.member.Member;
import com.vong.manidues.member.MemberRepository;
import com.vong.manidues.token.ClaimExtractor;
import com.vong.manidues.token.JwtService;
import com.vong.manidues.token.Token;
import com.vong.manidues.token.TokenRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DataJpaTest
@Slf4j
@ActiveProfiles("test")
class AuthenticationServiceJpaTest {
    private final AuthenticationRequest authRequest = AuthenticationFixture.AUTH_REQUEST;
    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;
    private final EntityManager entityManager;
    private AuthenticationService authService;
    @Mock
    private JwtService jwtService;
    @Mock
    private ClaimExtractor claimExtractor;
    @Mock
    private AuthenticationManager authManager;

    @Autowired
    public AuthenticationServiceJpaTest(MemberRepository memberRepository, TokenRepository tokenRepository, EntityManager entityManager) {
        this.memberRepository = memberRepository;
        this.tokenRepository = tokenRepository;
        this.entityManager = entityManager;
    }

    @BeforeEach
    void setUp() {
        authService = new AuthenticationService(
                memberRepository
                , tokenRepository
                , jwtService
                , claimExtractor
                , authManager
        );
    }

    @AfterEach
    void tearDown() {
        Query query = entityManager.createNativeQuery("SELECT * FROM token t", Token.class);
        List<?> resultList = query.getResultList();
        log.info(resultList.toString());

        tokenRepository.deleteAll();
    }

    @Test
    void authenticate() throws SQLException {
        // given
        var expectedAuth = mock(Authentication.class);
        when(authManager.authenticate(any(Authentication.class)))
                .thenReturn(expectedAuth);
        when(jwtService.generateAccessToken(any(Member.class)))
                .thenReturn("accessToken");
        when(jwtService.generateRefreshToken(any(Member.class)))
                .thenReturn("refreshToken");
        when(claimExtractor.extractExpiration(any())).thenReturn(new Date(System.currentTimeMillis() + 1_000_000L));

        // when
        var response = authService.authenticate(authRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("accessToken");
        assertThat(response.getRefreshToken()).isEqualTo("refreshToken");
        verify(authManager, times(1)).authenticate(any(Authentication.class));
        verify(jwtService, times(1)).generateAccessToken(any(Member.class));
        verify(jwtService, times(1)).generateRefreshToken(any(Member.class));
    }
}
