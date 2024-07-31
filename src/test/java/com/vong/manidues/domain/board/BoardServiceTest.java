package com.vong.manidues.domain.board;

import com.vong.manidues.domain.board.dto.BoardGetResponse;
import com.vong.manidues.domain.board.dto.BoardRegisterRequest;
import com.vong.manidues.domain.board.dto.BoardUpdateRequest;
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

import static com.vong.manidues.domain.member.MemberFixture.EMAIL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BoardServiceTest {
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
            var viewCount = 0L;
            var boardViewCount0 = Board.builder()
                    .member(mock(Member.class))
                    .viewCount(viewCount)
                    .build();
            var boardViewCountAdded = Board.builder()
                    .member(mock(Member.class))
                    .viewCount(++viewCount)
                    .build();
            var expected = BoardGetResponse.of(boardViewCountAdded);

            when(boardRepository.findById(any())).thenReturn(Optional.of(boardViewCount0));

            // when
            var returned = service.get(1L, mockRequest, mockResponse);

            // then
            assertThat(returned.getViewCount()).isEqualTo(expected.getViewCount());
        }

        @Test
        void getBoardViewCountNull() throws NoResourceFoundException {
            // given
            Long viewCount = null;
            var viewCountNull = Board.builder()
                    .member(mock(Member.class))
                    .viewCount(viewCount)
                    .build();
            var expected = BoardGetResponse.of(viewCountNull);

            when(boardRepository.findById(any())).thenReturn(Optional.of(viewCountNull));

            // when
            var returned = service.get(1L, mockRequest, mockResponse);

            // then
            assertThat(returned.getViewCount()).isEqualTo(expected.getViewCount()); // @EqualsAndHashCode from lombok.
        }

        @Test
        void register() {
            // given
            var boardId = 1L;
            var member = Member.builder()
                    .email(EMAIL)
                    .build();
            var savedBoard = Board.builder()
                    .id(boardId)
                    .viewCount(0L)
                    .build();
            var body = BoardRegisterRequest.builder()
                    .title("title")
                    .content("content")
                    .build();

            mockRequest.addHeader("Authorization", "Bearer some.valid.token");
            when(claimExtractor.extractUserEmail(any(String.class))).thenReturn(EMAIL);
            when(memberRepository.findByEmail(EMAIL)).thenReturn(Optional.of(member));
            when(boardRepository.save(any(Board.class))).thenReturn(savedBoard);

            // when
            var returned = service.register(mockRequest, body);

            // then
            assertThat(returned).isNotNull();
            assertThat(returned.getId()).isEqualTo(boardId);
            assertThat(returned.getMessage()).isEqualTo("게시물이 성공적으로 등록됐습니다.");
        }

        @Test
        void update() {
            // given
            var boardId = 1L;
            var member = Member.builder()
                    .email(EMAIL)
                    .build();
            var storedBoard = Board.builder()
                    .id(boardId)
                    .title("title")
                    .content("content")
                    .member(member)
                    .viewCount(0L)
                    .build();
            var requestBody = BoardUpdateRequest.builder()
                    .title("modified title")
                    .content("modified content")
                    .build();
            var updatedBoard = Board.builder()
                    .id(boardId)
                    .title("modified title")
                    .content("modified content")
                    .build();

            mockRequest.addHeader("Authorization", "Bearer some.valid.token");
            when(claimExtractor.extractUserEmail(any(String.class))).thenReturn(EMAIL);
            when(boardRepository.findById(boardId)).thenReturn(Optional.of(storedBoard));
            when(boardRepository.save(any(Board.class))).thenReturn(updatedBoard);

            // when
            var returned = service.update(boardId, mockRequest, requestBody);

            // then
            assertThat(returned.getMessage()).isEqualTo("게시물 수정이 정상적으로 처리됐습니다.");
        }

        @Test
        void delete() {
            // given
            Member member = Member.builder()
                    .email(EMAIL)
                    .build();
            Board storedBoard = Board.builder()
                    .title("title")
                    .content("content")
                    .member(member)
                    .viewCount(0L)
                    .build();

            mockRequest.addHeader("Authorization", "Bearer some.valid.token");
            var boardId = 1L;

            when(claimExtractor.extractUserEmail(any(String.class))).thenReturn(EMAIL);
            when(boardRepository.findById(boardId)).thenReturn(Optional.of(storedBoard));

            // when
            var returned = service.delete(boardId, mockRequest);

            // then
            assertThat(returned.getMessage()).isEqualTo("게시물 삭제가 정상적으로 처리됐습니다.");
        }
    }
}
