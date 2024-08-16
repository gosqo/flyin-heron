package com.vong.manidues.repository;

import com.vong.manidues.domain.fixture.MemberFixture;
import com.vong.manidues.service.AuthenticationService;
import com.vong.manidues.service.ClaimExtractor;
import com.vong.manidues.service.JwtService;
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
    class WithService extends DataJpaTestRepositoryDataInitializer {
        private final AuthenticationService authService;
        @MockBean
        private final AuthenticationManager authManager;

        @Autowired
        public WithService(
                MemberRepository memberRepository
                , BoardRepository boardRepository
                , CommentRepository commentRepository
                , TokenRepository tokenRepository
                , AuthenticationService authService
                , AuthenticationManager authManager
        ) {
            super(memberRepository, boardRepository, commentRepository, tokenRepository);
            this.authService = authService;
            this.authManager = authManager;
        }

        @BeforeEach
        void setUp() {
            initMember();
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
