package com.gosqo.flyinheron.global.utility;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class AuthHeaderUtilityTest {
    @Mock
    private MockHttpServletRequest mockRequestAuth;
    @Mock
    private MockHttpServletRequest mockRequestNoAuth;

    @BeforeEach
    void setUp() {
        mockRequestNoAuth = new MockHttpServletRequest();
        mockRequestAuth = new MockHttpServletRequest();

        mockRequestAuth.addHeader("Authorization", "Bearer some.valid.token");
    }

    @Test
    void isNotAuthenticatedAuthHeader() {
        assertThat(AuthHeaderUtility.isNotAuthenticated(mockRequestAuth)).isFalse();
    }

    @Test
    void isNotAuthenticatedNoAuthHeader() {
        assertThat(AuthHeaderUtility.isNotAuthenticated(mockRequestNoAuth)).isTrue();
    }

    @Test
    void extractJwtFromAuthHeader() {
        assertThat(AuthHeaderUtility.extractJwt(mockRequestAuth)).isEqualTo("some.valid.token");
    }

    @Test
    void extractJwtFromNoAuthHeader() {
        assertThatThrownBy(() -> AuthHeaderUtility.extractJwt(mockRequestNoAuth));
    }
}
