package com.vong.manidues.board;

import com.vong.manidues.board.dto.BoardGetResponse;
import com.vong.manidues.member.Member;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.vong.manidues.auth.AuthenticationFixture.*;
import static com.vong.manidues.member.Role.USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class BoardServiceTest {
    @InjectMocks
    private BoardServiceImpl service;
    @Mock
    private BoardRepository boardRepository;

    private final Member member = Member.builder()
            .nickname(NICKNAME)
            .password(PASSWORD)
            .email(MEMBER_EMAIL)
            .role(USER)
            .id(1L)
            .build();

    private final Board board = Board.builder()
            .title("title")
            .content("content")
            .viewCount(999L)
            .member(member)
            .build();

    private final Board board2 = Board.builder()
            .title("title")
            .content("content")
            .viewCount(null)
            .member(member)
            .build();

    @BeforeEach
    void setUp() {
    }

    @Test
    void get() {
        // given
        var expectedObj = BoardGetResponse.of(Board.builder()
                .title("title")
                .content("content")
                .viewCount(1000L)
                .member(member)
                .build());
        var request = mock(HttpServletRequest.class);
        var response = mock(HttpServletResponse.class);

        when(boardRepository.findById(1L))
                .thenReturn(Optional.of(board));

        // when
        var returns = service.get(1L, request, response);

        // then
        assertThat(returns).isEqualTo(expectedObj); // @EqualsAndHashCode from lombok.
    }

    @Test
    void get2() {
        // given
        var expectedObj = BoardGetResponse.of(board2);
        var request = mock(HttpServletRequest.class);
        var response = mock(HttpServletResponse.class);

        when(boardRepository.findById(1L))
                .thenReturn(Optional.of(board2));

        // when
        var returns = service.get(1L, request, response);

        // then
        assertThat(returns).isEqualTo(expectedObj); // @EqualsAndHashCode from lombok.
    }
}
