package com.vong.manidues.service;

import com.vong.manidues.domain.Board;
import com.vong.manidues.domain.Comment;
import com.vong.manidues.domain.Member;
import com.vong.manidues.repository.BoardRepository;
import com.vong.manidues.dto.comment.CommentRegisterRequest;
import com.vong.manidues.dto.comment.CommentUpdateRequest;
import com.vong.manidues.repository.CommentRepository;
import com.vong.manidues.repository.MemberRepository;
import com.vong.manidues.service.ClaimExtractor;
import com.vong.manidues.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * CommentService.getCommentSliceOf(Long, int) 메서드 테스트는
 * CommentRepository.findByBoardId(Long, Pageable) 메서드가 만들어내는
 * Slice 를 활용하기 위해 CommentRepositoryTest 내부 WithService 중첩 클래스 에서 진행.
 */
@ExtendWith({MockitoExtension.class})
class CommentServiceTest {
    private static final String USER_EMAIL = "some@valid.email";
    private static final String CONTENT = "Hello Comment.";
    private static final String MODIFIED_CONTENT = "Hello modified Comment.";
    private final Member member = Member.builder()
            .email(USER_EMAIL)
            .build();
    private final Comment storedComment = Comment.builder()
            .content(CONTENT)
            .member(member)
            .board(mock(Board.class))
            .build();
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private ClaimExtractor claimExtractor;
    @InjectMocks
    private CommentService service;

    /**
     * claimExtractor 에서 발생할 수 있는 예외는 모두
     * 앞선 Filter layer JwtExceptionFilter 클래스에서 처리.
     */
    private MockHttpServletRequest requestWithAuthHeader;

    @BeforeEach
    void setUp() {
        requestWithAuthHeader = new MockHttpServletRequest();
        requestWithAuthHeader.addHeader("Authorization", "Bearer some.valid.token");
    }

    @Nested
    @DisplayName("when register a comment,")
    class Register {

        @Test
        @DisplayName("throws exception with a comment with member not exist on database.")
        void registerWithNoneExistMember() {
            // given
            when(claimExtractor.extractUserEmail(anyString())).thenReturn("none@exist.email");
            when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());

            // when. then
            assertThatThrownBy(() -> service.register(requestWithAuthHeader, new CommentRegisterRequest()))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("throws exception with a comment references board not exist on database.")
        void registerWithNoneExistBoard() {
            // given
            var requestBody = CommentRegisterRequest.builder()
                    .boardId(-1L)
                    .build();

            when(claimExtractor.extractUserEmail(anyString())).thenReturn("none@exist.email");
            when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(mock(Member.class)));
            when(boardRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when. then
            assertThatThrownBy(() -> service.register(requestWithAuthHeader, requestBody))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("method-works-fine case is like")
        void register() {
            // given
            var boardId = 1L;
            var requestBody = CommentRegisterRequest.builder()
                    .boardId(boardId)
                    .content(CONTENT)
                    .build();

            when(claimExtractor.extractUserEmail(any(String.class))).thenReturn(USER_EMAIL);
            when(memberRepository.findByEmail(any(String.class))).thenReturn(Optional.of(mock(Member.class)));
            when(boardRepository.findById(eq(boardId))).thenReturn(Optional.of(mock(Board.class)));
            when(commentRepository.save(any(Comment.class))).thenReturn(storedComment);

            // when
            var result = service.register(requestWithAuthHeader, requestBody);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo(201);
            assertThat(result.getMessage()).isEqualTo("댓글을 등록했습니다.");
            assertThat(result.getComment().getContent()).isEqualTo(CONTENT);
        }
    }

    @Nested
    @DisplayName("when get a comment,")
    class Get {

        @Test
        @DisplayName("throws exception if the comment not exist on database.")
        void getCommentNotExist() {
            // given
            Long idNotExist = -1L;
            when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when, then
            assertThatThrownBy(() -> service.get(idNotExist))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("case method-works-fine is like.")
        void getComment() {
            Comment comment = Comment.builder()
                    .content("Hello, comment.")
                    // 해당 필드에 mock 혹은 실제 값을 넣지 않으면,
                    // service.get(Long id) 내부, CommentGetResponse.of(Comment entity) 에서
                    // entity.getMember.getNickname 호출 시, member 가 null 이면 NullPointerException 발생.
                    .member(mock(Member.class))
                    // 위와 마찬가지로 entity.getBoard().getId() 시에 NPE 발생.
                    .board(mock(Board.class))
                    .build();

            when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));

            var returned = service.get(99L);

            assertThat(returned).isNotNull();
        }
    }

    @Nested
    @DisplayName("when modify a comment,")
    class Modify {
        private final Comment modifiedComment = Comment.builder()
                .content(MODIFIED_CONTENT)
                .member(mock(Member.class))
                .board(mock(Board.class))
                .build();

        @Test
        @DisplayName("throws exception if writer(email) not matched.")
        void modifyCommentFromNotMatchedWriterEmail() {
            // given
            Long commentId = 1L;
            when(claimExtractor.extractUserEmail(anyString())).thenReturn("not@matched.email");
            when(commentRepository.findById(anyLong())).thenReturn(Optional.of(storedComment));

            // when, then
            assertThatThrownBy(() -> service.modify(commentId, requestWithAuthHeader, new CommentUpdateRequest()))
                    .isInstanceOf(AccessDeniedException.class);
        }

        @Test
        @DisplayName("throws exception requested comment not exist on database.")
        void modifyCommentNotExist() {
            // given
            Long idNotExist = -1L;
            when(claimExtractor.extractUserEmail(anyString())).thenReturn("none@exist.email");
            when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when, then
            assertThatThrownBy(() -> service.modify(idNotExist, requestWithAuthHeader, new CommentUpdateRequest()))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("case method-works-fine is like.")
        void modify() {
            var requestBody = CommentUpdateRequest.builder()
                    .content(MODIFIED_CONTENT)
                    .build();

            when(claimExtractor.extractUserEmail(any(String.class))).thenReturn(USER_EMAIL);
            when(commentRepository.findById(anyLong())).thenReturn(Optional.of(storedComment));
            when(commentRepository.save(any(Comment.class))).thenReturn(modifiedComment);

            // when
            var result = service.modify(anyLong(), requestWithAuthHeader, requestBody);

            // then
            assertThat(result.getStatus()).isEqualTo(200);
            assertThat(result.getUpdatedComment().getContent()).isEqualTo(MODIFIED_CONTENT);
        }
    }

    @Nested
    @DisplayName("when remove a comment,")
    class Remove {

        @Test
        @DisplayName("throws exception if writer(email) not matched.")
        void removeCommentFromNotMatchedWriterEmail() {
            // given
            Long commentId = 1L;
            when(claimExtractor.extractUserEmail(anyString())).thenReturn("not@matched.email");
            when(commentRepository.findById(anyLong())).thenReturn(Optional.of(storedComment));

            // when, then
            assertThatThrownBy(() -> service.remove(commentId, requestWithAuthHeader))
                    .isInstanceOf(AccessDeniedException.class);
        }

        @Test
        @DisplayName("throws exception requested comment not exist on database.")
        void removeCommentNotExist() {
            // given
            Long idNotExist = -1L;
            when(claimExtractor.extractUserEmail(anyString())).thenReturn("none@exist.email");
            when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when, then
            assertThatThrownBy(() -> service.remove(idNotExist, requestWithAuthHeader))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("case method-works-fine is like.")
        void remove() {
            Comment comment = Comment.builder()
                    .member(Member.builder()
                            .email("some@valid.email")
                            .build())
                    .build();

            // context 와 관련 없이 작동 가능한 static method 는
            // 기존 동작을 수행할 수 있도록 파라미터 및 변수에 사용할 값의 조건을 해당 메서드가 잘 작동할 수 있도록 맞춰줘야된다.
            // 해당 메서드의 동작을 제어하면, ArgumentMatchers 에 null 값이 넘어가면서 의도대로 동작하지 않음.
//            when(AuthHeaderUtility.extractJwt(eq(mockHttpRequest))).thenReturn("some.valid.token");

            // claimExtractor 가 사용하는 getSignInKey() 와 같이 전체 컨텍스트에 영향을 받는 메서드를 mock 으로 대체.
            // 테스트 의도대로 움직일 값을 반환하도록 지정한다.
            when(claimExtractor.extractUserEmail(any(String.class))).thenReturn("some@valid.email");
            when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));

            // when
            var returned = service.remove(1L, requestWithAuthHeader);

            // then
            assertThat(returned.getStatus()).isEqualTo(200);
        }
    }
}
