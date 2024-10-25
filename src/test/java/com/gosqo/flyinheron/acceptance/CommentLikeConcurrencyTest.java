package com.gosqo.flyinheron.acceptance;

import com.gosqo.flyinheron.domain.Member;
import com.gosqo.flyinheron.dto.CustomSliceImpl;
import com.gosqo.flyinheron.dto.comment.CommentGetResponse;
import com.gosqo.flyinheron.dto.commentlike.RegisterCommentLikeResponse;
import com.gosqo.flyinheron.global.data.TestDataRemover;
import com.gosqo.flyinheron.repository.BoardRepository;
import com.gosqo.flyinheron.repository.CommentRepository;
import com.gosqo.flyinheron.repository.MemberRepository;
import com.gosqo.flyinheron.service.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

import static com.gosqo.flyinheron.global.utility.HeadersUtility.buildHeadersWithToken;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class CommentLikeConcurrencyTest extends SpringBootTestBase {

    private static final String TARGET_URI_FORMAT = "/api/v1/comment-like/%d";
    private final JwtService jwtService;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private Map<String, Object> extraClaims;

    @Autowired
    CommentLikeConcurrencyTest(
            TestRestTemplate template
            , JwtService jwtService
            , MemberRepository memberRepository
            , BoardRepository boardRepository
            , CommentRepository commentRepository
            , TestDataRemover remover
    ) {
        super(template, remover);
        this.jwtService = jwtService;
        this.memberRepository = memberRepository;
        this.boardRepository = boardRepository;
        this.commentRepository = commentRepository;
    }

    @BeforeEach
    void setUp() {
        member = memberRepository.save(buildMember());
        boards = boardRepository.saveAll(buildBoards());
        comments = commentRepository.saveAll(buildComments());

        extraClaims = new HashMap<>();
    }

    private Map<String, Object> claimsPutMemberId(Member member) {
        extraClaims = new HashMap<>();
        extraClaims.put("id", member.getId());

        return extraClaims;
    }

    @Test
    void concurrency_test() throws InterruptedException, ExecutionException {
        LocalDateTime start = LocalDateTime.now();
        log.info("start. {}", start);

        Long targetBoardId = boards.get(0).getId();
        Long targetCommentIdFrom = comments.get(0).getId();

        extraClaims = claimsPutMemberId(member);
        final var token = jwtService.generateAccessToken(extraClaims, member);
        final var headers = buildHeadersWithToken(token);

        final int countToRegister = 6;
        final int threadPoolSize = 10;

        final List<Long> idsToRegisterCommentLike = new ArrayList<>();

        IntStream.range(0, countToRegister)
                .forEach((i) -> idsToRegisterCommentLike.add(targetCommentIdFrom + i));

        ScheduledExecutorService service = Executors.newScheduledThreadPool(threadPoolSize);
        CountDownLatch latch = new CountDownLatch(7);
        List<AtomicReference<LocalDateTime>> threadStarts = new ArrayList<>();
        AtomicReference<LocalDateTime> temp = new AtomicReference<>();

        idsToRegisterCommentLike
                .forEach((i) -> service.execute(
                        () -> {
                            LocalDateTime threadStart = LocalDateTime.now();
                            temp.set(threadStart);
                            threadStarts.add(temp);
                            log.info("thread-{} tarts {}", i, threadStart);

                            final var registerCommentLikeUri = String.format(TARGET_URI_FORMAT, i);

                            final var registerRequest = RequestEntity
                                    .post(registerCommentLikeUri)
                                    .headers(headers)
                                    .build();
                            template.exchange(registerRequest, RegisterCommentLikeResponse.class);
                            latch.countDown();
                        }
                ));

        final var getCommentsUri = String.format("/api/v1/board/%d/comments?page-number=1", targetBoardId);
        final var getCommentsRequest = RequestEntity
                .get(getCommentsUri)
                .build();

        AtomicReference<LocalDateTime> scheduled = new AtomicReference<>();

        final var future = service.schedule(() -> {

            scheduled.set(LocalDateTime.now());
            latch.countDown();

            return template.exchange(
                    getCommentsRequest
                    , new ParameterizedTypeReference<CustomSliceImpl<CommentGetResponse>>() {
                    }
            );
        }, 220L, TimeUnit.MILLISECONDS); // 200L 등록 요청이 트랜잭션 시작 후 조회 스케줄 작업 시작.

        latch.await();

        log.info(scheduled.get().toString());

        // execute 으로 시작한 스레드 시작 시간은 모두 같게 나오더라.
        // schedule delay 200 ~ 220 해두면 2 ~ 4 밀리세컨 정도 차이 나더라.
        threadStarts.forEach((item) -> {

            final long gap = scheduled.get().toInstant(ZoneOffset.ofHours(9)).toEpochMilli()
                    - item.get().toInstant(ZoneOffset.ofHours(9)).toEpochMilli();

            log.info(String.valueOf(gap));
        });

        Objects.requireNonNull(future.get().getBody())
                .getContent().stream()
                .filter((comment) -> idsToRegisterCommentLike.contains(comment.getId()))
                .forEach((filtered) -> {
                            log.info("commentId: {}, likeCount: {}", filtered.getId(), filtered.getLikeCount().toString());
                            assertThat(filtered.getLikeCount()).isEqualTo(1L);
                        }
                );
    }
}
