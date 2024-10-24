package com.gosqo.flyinheron.service.integrated;

import com.gosqo.flyinheron.domain.Comment;
import com.gosqo.flyinheron.domain.CommentLike;
import com.gosqo.flyinheron.domain.EntityStatus;
import com.gosqo.flyinheron.global.data.TestDataRemover;
import com.gosqo.flyinheron.repository.BoardRepository;
import com.gosqo.flyinheron.repository.CommentLikeRepository;
import com.gosqo.flyinheron.repository.CommentRepository;
import com.gosqo.flyinheron.repository.MemberRepository;
import com.gosqo.flyinheron.service.CommentLikeService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class CommentLikeServiceTest extends IntegratedServiceTestBase {
    private static final Long NOT_EXISTING_MEMBER_ID = 0L;
    private static final Long NOT_EXISTING_COMMENT_ID = 0L;
    private final CommentLikeService commentLikeService;
    private final CommentLikeRepository commentLikeRepository;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final TransactionTemplate transactionTemplate;

    private Long memberId;
    private Long commentIdHasCommentLike;
    private Long commentIdToRegisterItsLike;

    @Autowired
    public CommentLikeServiceTest(TestDataRemover remover
                                  , CommentLikeService commentLikeService
                                  , CommentLikeRepository commentLikeRepository
                                  , MemberRepository memberRepository
                                  , BoardRepository boardRepository
                                  , CommentRepository commentRepository
                                  , TransactionTemplate transactionTemplate
    ) {
        super(remover);
        this.commentLikeService = commentLikeService;
        this.commentLikeRepository = commentLikeRepository;
        this.memberRepository = memberRepository;
        this.boardRepository = boardRepository;
        this.commentRepository = commentRepository;
        this.transactionTemplate = transactionTemplate;
    }

    @BeforeEach
    @Transactional
    void setUp() {
        transactionTemplate.executeWithoutResult(what -> {

            member = memberRepository.save(buildMember());
            boards = boardRepository.saveAll(buildBoards());
            comments = commentRepository.saveAll(buildComments());
            commentLikes = commentLikeRepository.saveAll(buildCommentLikes());
        });

        log.info("==== Test data initialized. ====");

        memberId = member.getId();
        commentIdHasCommentLike = comments.get(0).getId();
        commentIdToRegisterItsLike = commentIdHasCommentLike + COMMENT_LIKE_COUNT;
    }

    @Nested
    class CommentLike_status_changes_by_register_and_delete {

        @Test
        void register_and_deleted_then_register_affects_column_likeCount_on_Comment() {
            Comment comment = commentRepository.findById(commentIdToRegisterItsLike).orElseThrow();
            assertThat(comment.getLikeCount()).isEqualTo(0L);

            var commentLike = commentLikeService.registerCommentLike(memberId, commentIdToRegisterItsLike);

            Comment foundRegistered = commentRepository.findById(commentIdToRegisterItsLike).orElseThrow();
            assertThat(foundRegistered.getLikeCount()).isEqualTo(1L);
            assertThat(commentLike.getStatus()).isEqualTo(EntityStatus.ACTIVE);

            commentLike = commentLikeService.deleteCommentLike(memberId, commentIdToRegisterItsLike);

            Comment foundDeleted = commentRepository.findById(commentIdToRegisterItsLike).orElseThrow();
            assertThat(foundDeleted.getLikeCount()).isEqualTo(0L);
            assertThat(commentLike.getStatus()).isEqualTo(EntityStatus.SOFT_DELETED);

            commentLike = commentLikeService.registerCommentLike(memberId, commentIdToRegisterItsLike);

            Comment foundRegisteredAgain = commentRepository.findById(commentIdToRegisterItsLike).orElseThrow();
            assertThat(foundRegisteredAgain.getLikeCount()).isEqualTo(1L);
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

            CommentLike found = commentLikeRepository
                    .findByMemberIdAndCommentId(memberId, commentIdHasCommentLike)
                    .orElseThrow();

            assertThat(found.getStatus()).isEqualTo(EntityStatus.SOFT_DELETED);

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

            Comment found = commentRepository.findById(commentIdToRegisterItsLike).orElseThrow();

            assertThat(found.getLikeCount()).isEqualTo(1L);
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
