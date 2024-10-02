package com.gosqo.flyinheron.domain;

import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
class CommentLikeTest extends EntityManagerDataInitializer {
    private Comment targetComment;

    @Autowired
    public CommentLikeTest(EntityManagerFactory emf) {
        super(emf);
    }

    @BeforeEach
    void setUp() {
        initComments(); // Comment 까지만 생성, CommentLike 개체 생성하지 않음.
        log.info("==== Test data initialized. ====");
        em.flush();

        targetComment = comments.get(0);
    }

    @Test
    void when_create_normally_its_Comment_likeCount_is_1L() {
        CommentLike commentLike = CommentLike.builder()
                .member(member)
                .comment(targetComment)
                .build();

        em.persist(commentLike);

        Comment foundComment = em.find(Comment.class, commentLike.getComment().getId());

        // CommentLike @PrePersist 를 통한 comment.likeCount 필드 값 증가
        assertThat(foundComment.getLikeCount()).isEqualTo(1L);
        assertThat(commentLike.getComment().getLikeCount()).isEqualTo(1L);
    }

    @Test
    void when_create_normally() {
        CommentLike commentLike = CommentLike.builder()
                .member(member)
                .comment(targetComment)
                .build();

        em.persist(commentLike);

        assertThat(commentLike.getStatus()).isEqualTo(EntityStatus.ACTIVE);
        assertThat(commentLike.getMember()).isEqualTo(member);
        assertThat(commentLike.getComment()).isEqualTo(targetComment);

        // CommentLike @PrePersist 를 통한 comment.likeCount 필드 값 증가
        assertThat(commentLike.getComment().getLikeCount()).isEqualTo(1L);

        assertThat(commentLike.getRegisteredAt()).isNotNull();
        assertThat(commentLike.getRegisteredAt()).isEqualTo(commentLike.getUpdatedAt());

        assertThat(commentLike.getContentModifiedAt()).isNull();
        assertThat(commentLike.getDeletedAt()).isNull();
    }

    private CommentLike buildCommentLike() {
        return CommentLike.builder()
                .member(member)
                .comment(targetComment)
                .build();
    }

    @Nested
    class CommentLike_domain_features {
    }

    @Nested
    class Cannot_create_CommentLike {

        @Test
        void without_member() {
            var commentLike = CommentLike.builder()
                    .comment(comments.get(0))
                    .build();

            assertThatThrownBy(() -> em.persist(commentLike));

            transaction.rollback();
        }

        @Test
        void without_comment() {
            var commentLike = CommentLike.builder()
                    .member(member)
                    .build();

            assertThatThrownBy(() -> em.persist(commentLike));

            transaction.rollback();
        }

        @Test
        void existing_combination_of_member_id_and_comment_id() {
            var commentLike1 = buildCommentLike();
            var commentLike2 = buildCommentLike();

            assertThat(commentLike1).isNotEqualTo(commentLike2);

            em.persist(commentLike1);

            assertThatThrownBy(() -> em.persist(commentLike2));

            transaction.rollback();
        }
    }
}
