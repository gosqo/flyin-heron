package com.vong.manidues.domain;

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
        initComments();
        log.info("==== Test data initialized. ====");
        targetComment = comments.get(0);
    }

    @Test
    void when_create_normally() {
        var commentLike = CommentLike.builder()
                .member(member)
                .comment(targetComment)
                .build();

        em.persist(commentLike);

        assertThat(commentLike.getStatus()).isEqualTo(EntityStatus.ACTIVE);
        assertThat(commentLike.getMember()).isEqualTo(member);
        assertThat(commentLike.getComment()).isEqualTo(targetComment);

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
