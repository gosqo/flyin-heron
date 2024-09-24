package com.vong.manidues.integrated;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vong.manidues.domain.Member;
import com.vong.manidues.dto.CustomSliceImpl;
import com.vong.manidues.dto.comment.CommentGetResponse;
import com.vong.manidues.dto.commentlike.RegisterCommentLikeResponse;
import com.vong.manidues.repository.BoardRepository;
import com.vong.manidues.repository.CommentLikeRepository;
import com.vong.manidues.repository.CommentRepository;
import com.vong.manidues.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static com.vong.manidues.global.utility.HttpUtility.*;
import static org.assertj.core.api.Assertions.assertThat;

public class CommentLikeConcurrencyTest extends SpringBootTestBase {

    private static final String TARGET_URI_FORMAT = "/api/v1/comment-like/%d";
    private final TestTokenBuilder tokenBuilder;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private Map<String, Object> extraClaims;

    @Autowired
    CommentLikeConcurrencyTest(
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
    void initData() {
        member = memberRepository.save(buildMember());
        boards = boardRepository.saveAll(buildBoards());
        comments = commentRepository.saveAll(buildComments());
    }

    @BeforeEach
    @Override
    void setUp() {
        initData();
        extraClaims = new HashMap<>();
    }

    @Override
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
    void concurrency_test() throws InterruptedException, ExecutionException {
        Long targetBoardId = boards.get(0).getId();
        Long targetCommentIdFrom = comments.get(0).getId();

        extraClaims = claimsPutMemberId(member);
        final var token = tokenBuilder.buildToken(extraClaims, member);
        final var headers = buildPostHeadersWithToken(token);

        final int countToRegister = 6;
        final int threadPoolSize = 10;

        final List<Long> idsToRegisterCommentLike = new ArrayList<>();

        IntStream.range(0, countToRegister)
                .forEach((i) -> idsToRegisterCommentLike.add(targetCommentIdFrom + i));

        ScheduledExecutorService service = Executors.newScheduledThreadPool(threadPoolSize);

        idsToRegisterCommentLike
                .forEach((i) -> service.execute(
                        () -> {
                            final var registerCommentLikeUri = String.format(TARGET_URI_FORMAT, i);

                            try {
                                final var registerRequest = buildPostRequestEntity(headers, null, registerCommentLikeUri);
                                template.exchange(registerRequest, RegisterCommentLikeResponse.class);
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException("JSON processing went wrong");
                            }
                        }
                ));

        final var getCommentsUri = String.format("/api/v1/board/%d/comments?page-number=1", targetBoardId);
        final var getCommentsRequest = buildGetRequestEntity(getCommentsUri);

        final var future = service.schedule(() -> template.exchange(
                getCommentsRequest
                , new ParameterizedTypeReference<CustomSliceImpl<CommentGetResponse>>() {
                }
        ), 100L, TimeUnit.MILLISECONDS);

        Objects.requireNonNull(future.get().getBody())
                .getContent().stream()
                .filter((comment) -> idsToRegisterCommentLike.contains(comment.getId()))
                .forEach((filtered) -> {
                            assertThat(filtered.getLikeCount()).isEqualTo(1L);
                        }
                );
    }
}
