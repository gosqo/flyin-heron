package com.vong.manidues.utility;

import com.vong.manidues.token.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthHeaderUtilityTest {
    private AuthHeaderUtility authHeaderUtility;
    @Mock
    private JwtService jwtService;
    @Mock
    private MockHttpServletRequest mockRequestAuth;
    @Mock
    private MockHttpServletRequest mockRequestNoAuth;

    @BeforeEach
    void setUp() {
        authHeaderUtility = new AuthHeaderUtility(jwtService);
        mockRequestNoAuth = new MockHttpServletRequest();
        mockRequestAuth = new MockHttpServletRequest();

        mockRequestAuth.addHeader("Authorization", "Bearer some.valid.token");
    }

    @Test
    void isNotAuthenticatedAuthHeader() {
        assertThat(authHeaderUtility.isNotAuthenticated(mockRequestAuth)).isFalse();
    }

    @Test
    void isNotAuthenticatedNoAuthHeader() {
        assertThat(authHeaderUtility.isNotAuthenticated(mockRequestNoAuth)).isTrue();
    }

    @Test
    void extractAuthHeader() {
        assertThat(authHeaderUtility.extractAuthHeader(mockRequestAuth)).isEqualTo("Bearer some.valid.token");
    }

    @Test
    void extractNullAuthHeader() {
        assertThat(authHeaderUtility.extractAuthHeader(mockRequestNoAuth)).isNull();
    }

    @Test
    void extractJwtFromAuthHeader() {
        assertThat(authHeaderUtility.extractJwtFromHeader(mockRequestAuth)).isEqualTo("some.valid.token");
    }

    @Test
    void extractJwtFromNoAuthHeader() {
        assertThatThrownBy(() -> authHeaderUtility.extractJwtFromHeader(mockRequestNoAuth));
    }

    @Test
    void extractEmailFromAuthHeader() {
        when(jwtService.extractUserEmail(any(String.class))).thenReturn("someUser@email.com");
        assertThat(authHeaderUtility.extractEmailFromHeader(mockRequestAuth)).isEqualTo("someUser@email.com");
    }

    @Test
    void extractEmailFromNoAuthHeader() {
        assertThatThrownBy(() -> authHeaderUtility.extractEmailFromHeader(mockRequestNoAuth));
    }
}