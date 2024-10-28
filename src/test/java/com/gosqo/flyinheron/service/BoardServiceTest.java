package com.gosqo.flyinheron.service;

import com.gosqo.flyinheron.domain.Board;
import com.gosqo.flyinheron.domain.Member;
import com.gosqo.flyinheron.dto.board.BoardGetResponse;
import com.gosqo.flyinheron.dto.board.BoardRegisterRequest;
import com.gosqo.flyinheron.dto.board.BoardUpdateRequest;
import com.gosqo.flyinheron.repository.BoardRepository;
import com.gosqo.flyinheron.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Optional;

import static com.gosqo.flyinheron.domain.fixture.MemberFixture.EMAIL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {
    @InjectMocks
    private BoardService service;
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

    @Nested
    class If_every_feature_works_fine {

        @Test
        void when_Board_has_null_of_viewCount() throws NoResourceFoundException {
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
                    .id(1L)
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
            when(memberRepository.findByEmail(any())).thenReturn(Optional.of(member));
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
                    .id(1L)
                    .email(EMAIL)
                    .build();
            Board storedBoard = Board.builder()
                    .member(member)
                    .title("title")
                    .content("content")
                    .viewCount(0L)
                    .build();

            mockRequest.addHeader("Authorization", "Bearer some.valid.token");
            var boardId = 1L;

            when(claimExtractor.extractUserEmail(any(String.class))).thenReturn(EMAIL);
            when(memberRepository.findByEmail(any())).thenReturn(Optional.of(member));
            when(boardRepository.findById(boardId)).thenReturn(Optional.of(storedBoard));

            // when
            var returned = service.delete(boardId, mockRequest);

            // then
            assertThat(returned.getMessage()).isEqualTo("게시물 삭제가 정상적으로 처리됐습니다.");
        }
    }
}
