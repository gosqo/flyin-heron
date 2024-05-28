package com.vong.manidues.board;

import com.vong.manidues.board.dto.BoardGetResponse;
import com.vong.manidues.board.dto.BoardRegisterRequest;
import com.vong.manidues.board.dto.BoardUpdateRequest;
import com.vong.manidues.board.dto.BoardUpdateResponse;
import com.vong.manidues.member.Member;
import com.vong.manidues.member.MemberRepository;
import com.vong.manidues.utility.AuthHeaderUtility;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Optional;

import static com.vong.manidues.auth.AuthenticationFixture.MEMBER_EMAIL;
import static com.vong.manidues.board.BoardUtility.buildMockBoard;
import static com.vong.manidues.member.MemberUtility.buildMockMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class BoardServiceTest {
    @InjectMocks
    private BoardServiceImpl service;

    @Mock
    private BoardRepository boardRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private AuthHeaderUtility authHeaderUtility;

    private MockHttpServletRequest mockRequest;
    private MockHttpServletResponse mockResponse;
    private final Member memberEntity = buildMockMember();
    private final Board board1 = buildMockBoard(1L, memberEntity, 999L);
    private final Board board1ViewCountAdded = buildMockBoard(1L, memberEntity, 1000L);
    private final Board board2 = buildMockBoard(2L, memberEntity, null);

    @BeforeEach
    void setUp() {
        mockRequest = new MockHttpServletRequest();
        mockResponse = new MockHttpServletResponse();
    }

    @Test
    void get() {
        // given
        var expectedObj = BoardGetResponse.of(board1ViewCountAdded);
        when(boardRepository.findById(1L)).thenReturn(Optional.of(board1));

        // when
        var returns = service.get(1L, mockRequest, mockResponse);

        // then
        assertThat(returns).isEqualTo(expectedObj); // @EqualsAndHashCode from lombok.
    }

    @Test
    void get2() {
        // given
        var expectedObj = BoardGetResponse.of(board2);
        when(boardRepository.findById(1L)).thenReturn(Optional.of(board2));

        // when
        var returns = service.get(1L, mockRequest, mockResponse);

        // then
        assertThat(returns).isEqualTo(expectedObj); // @EqualsAndHashCode from lombok.
    }

    @Test
    void register() {
        // given
        when(memberRepository.findByEmail(MEMBER_EMAIL)).thenReturn(Optional.of(memberEntity));
        when(boardRepository.save(any(Board.class))).thenReturn(board1);

        // when
        var result = service.register(MEMBER_EMAIL, BoardRegisterRequest.builder().build());

        // then
        assertThat(result).isEqualTo(1L);
    }

    @Test
    void update() {
        // given
        var mockRequest = new MockHttpServletRequest();
        mockRequest.addHeader("Authorization", "Bearer some.valid.token");
        var requestBody = BoardUpdateRequest.builder()
                .title("Updated title.")
                .content("Updated content.")
                .build();
        var updatedBoard = buildMockBoard(1L, memberEntity, 0L);

        when(boardRepository.findById(any(Long.class))).thenReturn(Optional.of(board1));
        when(authHeaderUtility.extractEmailFromHeader(any(HttpServletRequest.class))).thenReturn(MEMBER_EMAIL);
        when(boardRepository.save(any(Board.class))).thenReturn(updatedBoard);

        // when
        var result = service.update(1L, mockRequest, requestBody);

        // then
        assertThat(result).isEqualTo(BoardUpdateResponse.builder()
                .id(1L)
                .message("게시물 수정이 정상적으로 처리됐습니다.")
                .build()
        );
    }
}
