package com.vong.manidues.board;

import com.vong.manidues.board.dto.BoardDeleteResponse;
import com.vong.manidues.board.dto.BoardGetResponse;
import com.vong.manidues.board.dto.BoardUpdateResponse;
import com.vong.manidues.member.Member;
import com.vong.manidues.member.MemberRepository;
import com.vong.manidues.token.ClaimExtractor;
import com.vong.manidues.utility.AuthHeaderUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Optional;

import static com.vong.manidues.auth.AuthenticationFixture.MEMBER_EMAIL;
import static com.vong.manidues.board.BoardDtoUtility.buildBoardRegisterRequest;
import static com.vong.manidues.board.BoardDtoUtility.buildBoardUpdateRequest;
import static com.vong.manidues.board.BoardUtility.buildBoardAddedViewCount;
import static com.vong.manidues.board.BoardUtility.buildMockBoard;
import static com.vong.manidues.member.MemberUtility.buildMockMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BoardServiceTest {
    private final Member memberEntity = buildMockMember();
    private final Board boardActiveViewCount = buildMockBoard(1L, memberEntity, 999L);
    private final Board boardViewCountAdded = buildBoardAddedViewCount(boardActiveViewCount);
    private final Board boardViewCountNull = buildMockBoard(2L, memberEntity, null);

    @InjectMocks
    private BoardServiceImpl service;
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private AuthHeaderUtility authHeaderUtility;
    @Mock
    private ClaimExtractor claimExtractor;
    private MockHttpServletRequest mockRequest;
    private MockHttpServletResponse mockResponse;

    @BeforeEach
    void setUp() {
        mockRequest = new MockHttpServletRequest();
        mockResponse = new MockHttpServletResponse();
    }

    @Test
    void getBoardAddedViewCount() throws NoResourceFoundException {
        // given
        var expectedObj = BoardGetResponse.of(boardViewCountAdded);
        when(boardRepository.findById(any())).thenReturn(Optional.of(boardActiveViewCount));

        // when
        var returns = service.get(1L, mockRequest, mockResponse);

        // then
        assertThat(returns).isEqualTo(expectedObj); // @EqualsAndHashCode from lombok.
    }

    @Test
    void getBoardViewCountNull() throws NoResourceFoundException {
        // given
        var expectedObj = BoardGetResponse.of(boardViewCountNull);
        when(boardRepository.findById(any())).thenReturn(Optional.of(boardViewCountNull));

        // when
        var returns = service.get(1L, mockRequest, mockResponse);

        // then
        assertThat(returns).isEqualTo(expectedObj); // @EqualsAndHashCode from lombok.
    }

    @Test
    void register() {
        // given
        mockRequest.addHeader("Authorization", "Bearer some.valid.token");
        when(claimExtractor.extractUserEmail(any(String.class))).thenReturn(MEMBER_EMAIL);
        when(memberRepository.findByEmail(MEMBER_EMAIL)).thenReturn(Optional.of(memberEntity));
        when(boardRepository.save(any(Board.class))).thenReturn(boardActiveViewCount);

        // when
        var result = service.register(mockRequest, buildBoardRegisterRequest());

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getMessage()).isEqualTo("게시물이 성공적으로 등록됐습니다.");
    }

    @Test
    void update() {
        // given
        mockRequest.addHeader("Authorization", "Bearer some.valid.token");
        var boardId = 1L;
        var requestBody = buildBoardUpdateRequest();
        var updatedBoard = buildMockBoard(boardId, memberEntity, 0L);

        when(claimExtractor.extractUserEmail(any(String.class))).thenReturn(MEMBER_EMAIL);
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(boardActiveViewCount));
        when(boardRepository.save(any(Board.class))).thenReturn(updatedBoard);

        // when
        var result = service.update(boardId, mockRequest, requestBody);

        // then
        assertThat(result).isEqualTo(BoardUpdateResponse.builder()
                .id(1L)
                .message("게시물 수정이 정상적으로 처리됐습니다.")
                .build()
        );
    }

    @Test
    void delete() {
        // given
        mockRequest.addHeader("Authorization", "Bearer some.valid.token");
        var boardId = 1L;

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(boardActiveViewCount));
        when(claimExtractor.extractUserEmail(any(String.class))).thenReturn(MEMBER_EMAIL);

        // when
        var result = service.delete(boardId, mockRequest);

        // then
        assertThat(result).isEqualTo(BoardDeleteResponse.builder()
                .message("게시물 삭제가 정상적으로 처리됐습니다.")
                .build()
        );
    }
}
