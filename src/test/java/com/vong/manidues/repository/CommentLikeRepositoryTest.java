package com.vong.manidues.repository;

import com.vong.manidues.domain.Comment;
import com.vong.manidues.domain.CommentLike;
import com.vong.manidues.domain.EntityStatus;
import com.vong.manidues.service.CommentLikeService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@Import({CommentLikeService.class})
class CommentLikeRepositoryTest extends RepositoryTestBase {
    private static final int LIKED_COMMENTS_PAGE_SIZE = 10;
    private static final Long NOT_EXISTING_MEMBER_ID = 0L;
    private static final Long NOT_EXISTING_COMMENT_ID = 0L;
    private final CommentLikeService commentLikeService;
    private final CommentLikeRepository commentLikeRepository;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private Long memberId;
    private Long commentIdHasCommentLike;
    private Long commentIdToRegisterItsLike;

    @Autowired
    public CommentLikeRepositoryTest(
            CommentLikeService commentLikeService,
            CommentLikeRepository commentLikeRepository,
            MemberRepository memberRepository,
            BoardRepository boardRepository,
            CommentRepository commentRepository
    ) {
        this.commentLikeService = commentLikeService;
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

        memberId = member.getId();
        commentIdHasCommentLike = comments.get(0).getId();
        commentIdToRegisterItsLike = commentIdHasCommentLike + COMMENT_LIKE_COUNT;
    }

    @Test
    void findByMemberId() {
        int pageNumber = 1;
        Slice<Comment> commentsLikedByMember = commentLikeRepository.findByMemberId(
                        memberId, getLikedCommentsPageRequest(pageNumber))
                .map(CommentLike::getComment);

        commentsLikedByMember.getContent().forEach((item) -> log.info("{}", item));
    }

    @Test
    void
    findByMemberIdAndCommentId() {
        CommentLike foundCommentLike = commentLikeRepository.findByMemberIdAndCommentId(memberId, commentIdHasCommentLike).orElseThrow();

        assertThat(foundCommentLike).isNotNull();
    }

    @Nested
    class Service_Included {

        @Nested
        class CommentLike_status_changes_by_register_and_delete {

            @Test
            void register_and_deleted_then_register_affects_column_likeCount_on_Comment() {
                Comment comment = commentRepository.findById(commentIdToRegisterItsLike).orElseThrow();

                assertThat(comment.getLikeCount()).isEqualTo(0L);

                var commentLike = commentLikeService.registerCommentLike(memberId, commentIdToRegisterItsLike);

                assertThat(comment.getLikeCount()).isEqualTo(1L);
                assertThat(commentLike.getStatus()).isEqualTo(EntityStatus.ACTIVE);

                commentLike = commentLikeService.deleteCommentLike(memberId, commentIdToRegisterItsLike);

                assertThat(comment.getLikeCount()).isEqualTo(0L);
                assertThat(commentLike.getStatus()).isEqualTo(EntityStatus.SOFT_DELETED);

                commentLike = commentLikeService.registerCommentLike(memberId, commentIdToRegisterItsLike);

                assertThat(comment.getLikeCount()).isEqualTo(1L);
                assertThat(commentLike.getStatus()).isEqualTo(EntityStatus.ACTIVE);
            }
        }

        @Nested
        class DeleteCommentLike {

            @Test
            void delete_registered_commentLike() {
                CommentLike registered = commentLikeRepository
                        .findByMemberIdAndCommentId(memberId, commentIdHasCommentLike)
                        .orElseThrow();

                assertThat(registered).isNotNull();

                commentLikeService.deleteCommentLike(memberId, commentIdHasCommentLike);

                assertThat(registered).isNotNull();
                assertThat(registered.getStatus()).isEqualTo(EntityStatus.SOFT_DELETED);

                boolean hasLike = commentLikeService.hasLike(memberId, registered.getId());

                assertThat(hasLike).isFalse();
            }
        }

        @Nested
        class RegisterCommentLike {

            @Test
            void throws_NoSuchElementException_when_member_not_exist() {
                assertThatThrownBy(() ->
                        commentLikeService.registerCommentLike(NOT_EXISTING_MEMBER_ID, commentIdToRegisterItsLike))
                        .isInstanceOf(NoSuchElementException.class);
            }

            @Test
            void throws_NoSuchElementException_when_comment_not_exist() {
                assertThatThrownBy(() ->
                        commentLikeService.registerCommentLike(memberId, NOT_EXISTING_COMMENT_ID))
                        .isInstanceOf(NoSuchElementException.class);
            }

            @Test
            void when_register_CommentLike_value_of_likeCount_on_Comment_increases() {
                Comment comment = commentRepository.findById(commentIdToRegisterItsLike).orElseThrow();

                assertThat(comment.getLikeCount()).isEqualTo(0L);

                commentLikeService.registerCommentLike(memberId, comment.getId());

                assertThat(comment.getLikeCount()).isEqualTo(1L);
            }

            @Test
            void when_try_to_register_existing_CommentLike_with_duplicated_memberId_and_commentId_returns_itself() {
                CommentLike returned = commentLikeService.registerCommentLike(memberId, commentIdHasCommentLike);

                assertThat(returned.getMember().getId()).isEqualTo(memberId);
                assertThat(returned.getComment().getId()).isEqualTo(commentIdHasCommentLike);
                assertThat(returned.isActive()).isTrue();
            }
        }

        @Nested
        class HasLike {

            @Test
            void if_registered_but_softDeleted_returns_false() {
                commentLikeService.deleteCommentLike(memberId, commentIdHasCommentLike);
                boolean hasLike = commentLikeService.hasLike(memberId, commentIdHasCommentLike);

                assertThat(hasLike).isFalse();
            }

            @Test
            void if_member_not_exist_returns_false() {
                assertThat(commentLikeService.hasLike(NOT_EXISTING_MEMBER_ID, comments.get(0).getId())).isFalse();
            }

            @Test
            void if_comment_not_exist_returns_false() {
                assertThat(commentLikeService.hasLike(memberId, NOT_EXISTING_COMMENT_ID)).isFalse();
            }

            @Test
            void if_comment_and_member_exist_returns_true() {
                assertThat(commentLikeService.hasLike(memberId, comments.get(0).getId())).isTrue();
            }
        }
    }
}