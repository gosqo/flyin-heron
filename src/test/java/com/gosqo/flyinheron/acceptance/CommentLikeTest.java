package com.gosqo.flyinheron.acceptance;

import com.gosqo.flyinheron.domain.Comment;
import com.gosqo.flyinheron.domain.Member;
import com.gosqo.flyinheron.dto.commentlike.DeleteCommentLikeResponse;
import com.gosqo.flyinheron.dto.commentlike.HasCommentLikeResponse;
import com.gosqo.flyinheron.dto.commentlike.RegisterCommentLikeResponse;
import com.gosqo.flyinheron.global.data.TestDataRemover;
import com.gosqo.flyinheron.repository.BoardRepository;
import com.gosqo.flyinheron.repository.CommentLikeRepository;
import com.gosqo.flyinheron.repository.CommentRepository;
import com.gosqo.flyinheron.repository.MemberRepository;
import com.gosqo.flyinheron.service.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;

import java.util.HashMap;
import java.util.Map;

import static com.gosqo.flyinheron.global.utility.HeadersUtility.buildHeadersWithToken;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class CommentLikeTest extends SpringBootTestBase {
    private static final String TARGET_URI_FORMAT = "/api/v1/comment-like/%d";
    private final JwtService jwtService;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;

    private Long commentIdHasCommentLike;
    private Long commentIdToRegisterItsLike;
    private Map<String, Object> extraClaims;

    @Autowired
    public CommentLikeTest(
            TestRestTemplate template
            , JwtService jwtService
            , MemberRepository memberRepository
            , BoardRepository boardRepository
            , CommentRepository commentRepository
            , CommentLikeRepository commentLikeRepository
            , TestDataRemover remover
    ) {
        super(template, remover);
        this.jwtService = jwtService;
        this.memberRepository = memberRepository;
        this.boardRepository = boardRepository;
        this.commentRepository = commentRepository;
        this.commentLikeRepository = commentLikeRepository;
    }

    @BeforeEach
    void setUp() {
        member = memberRepository.save(buildMember());
        boards = boardRepository.saveAll(buildBoards());
        comments = commentRepository.saveAll(buildComments());
        commentLikes = commentLikeRepository.saveAll(buildCommentLikes());

        commentRepository.saveAll(comments); // commentLike @PrePersist 수행 내용 DB 에 적용
        log.info("==== Test data initialized. ====");

        commentIdHasCommentLike = comments.get(0).getId();
        commentIdToRegisterItsLike = commentIdHasCommentLike + COMMENT_LIKE_COUNT;
    }

    private Map<String, Object> claimsPutMemberId(Member member) {
        extraClaims = new HashMap<>();
        extraClaims.put("id", member.getId());

        return extraClaims;
    }

    @Test
    void likeCount_of_comment_which_has_existing_CommentLike_is_1L() {
        Comment comment = commentRepository.findById(commentIdHasCommentLike).orElseThrow();
        assertThat(comment.getLikeCount()).isEqualTo(1L);
    }

    @Test
    void register_existing_CommentLike() {
        String uri = String.format(TARGET_URI_FORMAT, commentIdHasCommentLike);
        extraClaims = claimsPutMemberId(member);

        String token = jwtService.generateAccessToken(extraClaims, member);

        final var headers = buildHeadersWithToken(token);
        final var request = RequestEntity
                .post(uri)
                .headers(headers)
                .build();

        final var response = template.exchange(request, RegisterCommentLikeResponse.class);

        Comment foundComment = commentRepository.findById(commentIdHasCommentLike).orElseThrow();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(foundComment.getLikeCount()).isEqualTo(1L);
    }

    @Test
    void registerCommentLike() {
        Comment storedComment = commentRepository.findById(commentIdToRegisterItsLike).orElseThrow();

        assertThat(storedComment.getLikeCount()).isEqualTo(0L);

        String uri = String.format(TARGET_URI_FORMAT, commentIdToRegisterItsLike);
        extraClaims = claimsPutMemberId(member);

        String token = jwtService.generateAccessToken(extraClaims, member);

        final var headers = buildHeadersWithToken(token);
        final var request = RequestEntity
                .post(uri)
                .headers(headers)
                .build();

        final var response = template.exchange(request, RegisterCommentLikeResponse.class);

        Comment foundComment = commentRepository.findById(commentIdToRegisterItsLike).orElseThrow();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(foundComment.getLikeCount()).isEqualTo(1L);
    }

    @Test
    void hasLike() {
        Long commentId = comments.get(0).getId();
        String uri = String.format(TARGET_URI_FORMAT, commentId);
        extraClaims = claimsPutMemberId(member);

        String token = jwtService.generateAccessToken(extraClaims, member);
        final var headers = buildHeadersWithToken(token);
        final var request = RequestEntity
                .get(uri)
                .headers(headers)
                .build();

        final var response = template.exchange(request, HasCommentLikeResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void deleteCommentLike() {
        Long commentId = comments.get(0).getId();
        String uri = String.format(TARGET_URI_FORMAT, commentId);
        extraClaims = claimsPutMemberId(member);

        String token = jwtService.generateAccessToken(extraClaims, member);

        final var headers = buildHeadersWithToken(token);
        final var request = RequestEntity
                .delete(uri)
                .headers(headers)
                .build();

        final var response = template.exchange(request, DeleteCommentLikeResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
