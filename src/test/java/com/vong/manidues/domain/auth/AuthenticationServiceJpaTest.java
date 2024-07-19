package com.vong.manidues.domain.auth;

import com.vong.manidues.DataJpaTestJpaRepositoryBase;
import com.vong.manidues.domain.board.BoardRepository;
import com.vong.manidues.domain.comment.CommentRepository;
import com.vong.manidues.domain.member.MemberFixture;
import com.vong.manidues.domain.member.MemberRepository;
import com.vong.manidues.domain.token.ClaimExtractor;
import com.vong.manidues.domain.token.JwtService;
import com.vong.manidues.domain.token.TokenRepository;
import lombok.extern.slf4j.Slf4j;
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
class AuthenticationServiceJpaTest/* extends DataJpaTestJpaRepositoryBase*/ {

    /* @Autowired
     public AuthenticationServiceJpaTest(
             MemberRepository memberRepository
             , BoardRepository boardRepository
             , CommentRepository commentRepository
             , TokenRepository tokenRepository
     ) {
         super(memberRepository, boardRepository, commentRepository, tokenRepository);
     }
 */
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
    class WithService extends DataJpaTestJpaRepositoryBase {
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
            assertThat(response.getAccessToken()).contains("eyJhbGci");
            assertThat(response.getRefreshToken()).contains("eyJhbGci");
            assertThat(tokenRepository.findByToken(response.getRefreshToken()).orElseThrow()).isNotNull();
        }
    }
}
