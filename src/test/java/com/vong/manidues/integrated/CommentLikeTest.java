package com.vong.manidues.integrated;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vong.manidues.domain.Comment;
import com.vong.manidues.domain.Member;
import com.vong.manidues.dto.commentlike.DeleteCommentLikeResponse;
import com.vong.manidues.dto.commentlike.HasCommentLikeResponse;
import com.vong.manidues.dto.commentlike.RegisterCommentLikeResponse;
import com.vong.manidues.repository.BoardRepository;
import com.vong.manidues.repository.CommentLikeRepository;
import com.vong.manidues.repository.CommentRepository;
import com.vong.manidues.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;

import java.util.HashMap;
import java.util.Map;

import static com.vong.manidues.global.utility.HttpUtility.*;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class CommentLikeTest extends SpringBootTestBase {
    private static final String TARGET_URI_FORMAT = "/api/v1/comment-like/%d";
    private final TestTokenBuilder tokenBuilder;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;

    @PersistenceContext
    private EntityManager em;

    private Long memberId;
    private Long commentIdHasCommentLike;
    private Long commentIdToRegisterItsLike;
    private Map<String, Object> extraClaims;

    @Autowired
    public CommentLikeTest(
            TestRestTemplate template,
            TestTokenBuilder tokenBuilder,
            MemberRepository memberRepository,
            BoardRepository boardRepository,
            CommentRepository commentRepository,
            CommentLikeRepository commentLikeRepository
    ) {
        super(template);
        this.tokenBuilder = tokenBuilder;
        this.memberRepository = memberRepository;
        this.boardRepository = boardRepository;
        this.commentRepository = commentRepository;
        this.commentLikeRepository = commentLikeRepository;
    }

    @Override
    @Transactional
    void initData() {
        member = memberRepository.save(buildMember());
        boards = boardRepository.saveAll(buildBoards());
        comments = commentRepository.saveAll(buildComments());
        commentLikes = commentLikeRepository.saveAll(buildCommentLikes());
        log.info("EntityManger contains Comment of which id = 1? {}", em.contains(comments.get(0)));

        commentRepository.saveAll(comments); // commentLike @PrePersist 수행 내용 DB 에 적용
    }

    @Override
    @BeforeEach
    void setUp() {
        initData();
        log.info("==== Test data initialized. ====");

        memberId = member.getId();
        commentIdHasCommentLike = comments.get(0).getId();
        commentIdToRegisterItsLike = commentIdHasCommentLike + COMMENT_LIKE_COUNT;
    }

    @Override
    @AfterEach
    void tearDown() {
        commentLikeRepository.deleteAll();
        commentRepository.deleteAll();
        boardRepository.deleteAll();
        memberRepository.deleteAll();
    }

    private Map<String, Object> claimsPutMemberId(Member member) {
        extraClaims = new HashMap<>();
        extraClaims.put("id", member.getId());

        return extraClaims;
    }

    @Test
    void with_existing_CommentLike_Comment_likeCount_is_1L() {
        Comment comment = commentRepository.findById(commentIdHasCommentLike).orElseThrow();
        assertThat(comment.getLikeCount()).isEqualTo(1L);
    }

    @Test
    void register_existing_CommentLike() throws JsonProcessingException {
        String uri = String.format(TARGET_URI_FORMAT, commentIdHasCommentLike);
        extraClaims = claimsPutMemberId(member);

        String token = tokenBuilder.buildToken(extraClaims, member);

        final var headers = buildPostHeadersWithToken(token);
        final var request = buildPostRequestEntity(headers, null, uri);
        final var response = template.exchange(request, RegisterCommentLikeResponse.class);

        Comment foundComment = commentRepository.findById(commentIdHasCommentLike).orElseThrow();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(foundComment.getLikeCount()).isEqualTo(1L);
    }

    @Test
    void registerCommentLike() throws JsonProcessingException {
        Comment storedComment = commentRepository.findById(commentIdToRegisterItsLike).orElseThrow();

        assertThat(storedComment.getLikeCount()).isEqualTo(0L);

        String uri = String.format(TARGET_URI_FORMAT, commentIdToRegisterItsLike);
        extraClaims = claimsPutMemberId(member);

        String token = tokenBuilder.buildToken(extraClaims, member);

        final var headers = buildPostHeadersWithToken(token);
        final var request = RequestEntity.post(uri)
                .headers(headers)
                .build();
//         buildPostRequestEntity(headers, null, uri);
        final var response = template.exchange(request, RegisterCommentLikeResponse.class);

        Comment foundComment = commentRepository.findById(commentIdToRegisterItsLike).orElseThrow();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(foundComment.getLikeCount()).isEqualTo(1L);
    }

    @Test
    void hasLike() throws JsonProcessingException {
        Long commentId = comments.get(0).getId();
        String uri = String.format(TARGET_URI_FORMAT, commentId);
        extraClaims = claimsPutMemberId(member);

        String token = tokenBuilder.buildToken(extraClaims, member);
        final var headers = buildGetHeadersWithToken(token);
        final var request = buildGetRequestEntity(headers, uri);

        final var response = template.exchange(request, HasCommentLikeResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void deleteCommentLike() throws JsonProcessingException {
        Long commentId = comments.get(0).getId();
        String uri = String.format(TARGET_URI_FORMAT, commentId);
        extraClaims = claimsPutMemberId(member);

        String token = tokenBuilder.buildToken(extraClaims, member);

        final var headers = buildGetHeadersWithToken(token);
        final var request = buildDeleteRequestEntity(headers, uri);

        final var response = template.exchange(request, DeleteCommentLikeResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
