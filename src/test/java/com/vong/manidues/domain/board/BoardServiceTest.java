package com.vong.manidues.domain.board;

import com.vong.manidues.domain.board.dto.BoardGetResponse;
import com.vong.manidues.domain.board.dto.BoardUpdateResponse;
import com.vong.manidues.domain.member.Member;
import com.vong.manidues.domain.member.MemberRepository;
import com.vong.manidues.domain.token.ClaimExtractor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.vong.manidues.domain.auth.AuthenticationFixture.MEMBER_EMAIL;
import static com.vong.manidues.domain.member.MemberUtility.buildMockMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BoardServiceTest {
    private final Member memberEntity = buildMockMember();
    private final Board boardActiveViewCount = BoardUtility.buildMockBoard(1L, memberEntity, 999L);
    private final Board boardViewCountAdded = BoardUtility.buildBoardAddedViewCount(boardActiveViewCount);
    private final Board boardViewCountNull = BoardUtility.buildMockBoard(2L, memberEntity, null);

    @InjectMocks
    private BoardServiceImpl service;
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private MemberRepository memberRepository;
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
    @DisplayName("if page that repository returned is empty, throws NoResourceFoundException")
    void emptyContentThrows() {
        int requestPage = 2; // 요청에 들어갈 페이지. 1 부터 시작.

        int size = 2; // repository 계층에서 페이지 나눌 때 사용할 size
        int page = 2; // repository 계층에서 제공할 pageable 인덱스

        // repository.findAll(Pageable) 을 통해 만들어질 Page<Board> 제작을 위한 변수들.
        Pageable pageable = PageRequest.of(page, size);
        List<Board> emptyList = Collections.EMPTY_LIST;
        Page<Board> emptyContentPage = new PageImpl<>(emptyList, pageable, 4);

        // Page<Board> mockPage = mock(Page.class); // mockPage.getContent().isEmpty() ==> true.

        when(boardRepository.findAll(any(PageRequest.class))).thenReturn(emptyContentPage);

        assertThatThrownBy(() -> service.getBoardPage(requestPage));
    }

    @Nested
    @DisplayName("If every feature works fine,")
    class IfWorksFine {

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
            var result = service.register(mockRequest, BoardDtoUtility.buildBoardRegisterRequest());

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
            var requestBody = BoardDtoUtility.buildBoardUpdateRequest();
            var updatedBoard = BoardUtility.buildMockBoard(boardId, memberEntity, 0L);

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
            assertThat(result.getMessage()).isEqualTo("게시물 삭제가 정상적으로 처리됐습니다.");
        }

    }
}
