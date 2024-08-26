package com.vong.manidues.repository;

import com.vong.manidues.domain.Comment;
import com.vong.manidues.domain.CommentLike;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class CommentLikeRepositoryTest extends RepositoryTestBase {
    private static final int LIKED_COMMENTS_PAGE_SIZE = 10;
    private final CommentLikeRepository commentLikeRepository;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public CommentLikeRepositoryTest(
            CommentLikeRepository commentLikeRepository,
            MemberRepository memberRepository,
            BoardRepository boardRepository,
            CommentRepository commentRepository
    ) {
        this.commentLikeRepository = commentLikeRepository;
        this.memberRepository = memberRepository;
        this.boardRepository = boardRepository;
        this.commentRepository = commentRepository;
    }

    private static PageRequest getLikedCommentsPageRequest(int pageNumber) {
        pageNumber = pageNumber - 1;
        Sort sort = Sort.by(Sort.Direction.ASC, "id");

        return PageRequest.of(pageNumber, LIKED_COMMENTS_PAGE_SIZE, sort);
    }

    @Override
    void initData() {
        member = memberRepository.save(buildMember());
        boards = boardRepository.saveAll(buildBoards());
        comments = commentRepository.saveAll(buildComments());
        commentLikes = commentLikeRepository.saveAll(buildCommentLikes());
    }

    @Override
    @BeforeEach
    void setUp() {
        initData();
        log.info("==== Test data initialized. ====");
    }

    @Test
    void findByMemberId() {
        int pageNumber = 1;
        Slice<Comment> commentsLikedByMember = commentLikeRepository.findByMemberId(
                        member.getId(), getLikedCommentsPageRequest(pageNumber))
                .map(CommentLike::getComment);
        commentsLikedByMember.getContent().forEach((item) -> log.info("{}", item));
    }

    @Test
    void
    findByMemberIdAndCommentId() {
        Long memberId = member.getId();
        Long commentId = comments.get(0).getId();
        CommentLike foundCommentLike = commentLikeRepository.findByMemberIdAndCommentId(memberId, commentId).orElseThrow();

        assertThat(foundCommentLike).isNotNull();
    }
}