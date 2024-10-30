package com.gosqo.flyinheron.repository;

import com.gosqo.flyinheron.domain.Comment;
import com.gosqo.flyinheron.domain.CommentLike;
import com.gosqo.flyinheron.service.CommentLikeService;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Import({CommentLikeService.class})
class CommentLikeRepositoryTest extends RepositoryTestBase {
    private static final int LIKED_COMMENTS_PAGE_SIZE = 10;
    private final CommentLikeRepository commentLikeRepository;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final EntityManager em;

    private Long memberId;
    private Long commentIdHasCommentLike;

    @Autowired
    CommentLikeRepositoryTest(
            CommentLikeRepository commentLikeRepository,
            MemberRepository memberRepository,
            BoardRepository boardRepository,
            CommentRepository commentRepository
            , EntityManager em
    ) {
        this.commentLikeRepository = commentLikeRepository;
        this.memberRepository = memberRepository;
        this.boardRepository = boardRepository;
        this.commentRepository = commentRepository;
        this.em = em;
    }

    private static PageRequest getLikedCommentsPageRequest(int pageNumber) {
        pageNumber = pageNumber - 1;
        Sort sort = Sort.by(Sort.Direction.ASC, "id");

        return PageRequest.of(pageNumber, LIKED_COMMENTS_PAGE_SIZE, sort);
    }

    @BeforeEach
    void setUp() {
        member = memberRepository.save(buildMember());
        boards = boardRepository.saveAll(buildBoards());
        comments = commentRepository.saveAll(buildComments());
        commentLikes = commentLikeRepository.saveAll(buildCommentLikes());

        em.flush(); // commentLike @PrePersist 에 의한 comment.addLikeCount 적용 update 쿼리 나감.
        em.clear();
        log.info("==== Test data initialized. ====");

        memberId = member.getId();
        commentIdHasCommentLike = comments.get(0).getId();
    }

    @Test
    void comment_referenced_by_CommentLike_has_1_likeCount() {
        Comment comment = commentRepository.findById(commentIdHasCommentLike).orElseThrow();
        assertThat(comment.getLikeCount()).isEqualTo(1L);
    }

    @Test
    void findByMemberId() {
        int pageNumber = 1;
        Slice<Comment> commentsLikedByMember = commentLikeRepository.findByMemberId(
                memberId, getLikedCommentsPageRequest(pageNumber)
        ).map(CommentLike::getComment);

        commentsLikedByMember.getContent().forEach((item) -> log.info("{}", item));
    }

    @Test
    void
    findByMemberIdAndCommentId() {
        CommentLike foundCommentLike = commentLikeRepository.findByMemberIdAndCommentId(
                memberId
                , commentIdHasCommentLike
        ).orElseThrow();

        assertThat(foundCommentLike).isNotNull();
    }
}
