package com.gosqo.flyinheron.repository;

import com.gosqo.flyinheron.domain.fixture.MemberFixture;
import com.gosqo.flyinheron.service.AuthenticationService;
import com.gosqo.flyinheron.service.ClaimExtractor;
import com.gosqo.flyinheron.service.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.mock.mockito.SpyBeans;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Slf4j
class AuthenticationServiceJpaTest {

    @Nested
    @DisplayName("with imported AuthenticationService")
    @Import(AuthenticationService.class)
    @SpyBeans(
            @SpyBean(
                    classes = {
                            JwtService.class
                            , ClaimExtractor.class
                    }
            )
    )
    class WithService extends RepositoryTestBase {
        private final AuthenticationService authService;
        @MockBean
        private final AuthenticationManager authManager;

        private final MemberRepository memberRepository;
        private final TokenRepository tokenRepository;

        @Autowired
        public WithService(
                MemberRepository memberRepository
                , TokenRepository tokenRepository
                , AuthenticationService authService
                , AuthenticationManager authManager
        ) {
            this.memberRepository = memberRepository;
            this.tokenRepository = tokenRepository;
            this.authService = authService;
            this.authManager = authManager;
        }

        @Override
        void initData() {
            member = memberRepository.save(buildMember());
        }

        @Override
        @BeforeEach
        void setUp() {
            initData();
            log.info("==== Test data initialized. ====");
        }

        @Test
        void authenticate() throws SQLException {
            // given
            final var expectedAuth = UsernamePasswordAuthenticationToken
                    .authenticated(member.getEmail(), member.getPassword(), member.getAuthorities());

            when(authManager.authenticate(any(Authentication.class))).thenReturn(expectedAuth);

            // when
            final var response = authService.authenticate(MemberFixture.AUTH_REQUEST);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getAccessToken()).isNotBlank();
            assertThat(response.getRefreshToken()).isNotBlank();
            assertThat(tokenRepository.findByToken(response.getRefreshToken()).orElseThrow()).isNotNull();
        }
    }
}
