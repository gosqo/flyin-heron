package com.vong.manidues.domain;

import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
class CommentLikeTest extends DataJpaTestEntityManagerBase {

    @Autowired
    public CommentLikeTest(EntityManagerFactory emf) {
        super(emf);
    }

    @Test
    void when_create_normally() {
        var targetComment = comments[0];
        var commentLike = CommentLike.builder()
                .member(member)
                .comment(targetComment)
                .build();

        em.persist(commentLike);

        assertThat(commentLike.getStatus()).isEqualTo(EntityStatus.ACTIVE);
        assertThat(commentLike.getMember()).isEqualTo(member);
        assertThat(commentLike.getComment()).isEqualTo(targetComment);

        assertThat(commentLike.getRegisteredAt()).isNotNull();
        assertThat(commentLike.getUpdatedAt()).isNotNull();
        assertThat(commentLike.getRegisteredAt()).isEqualTo(commentLike.getUpdatedAt());

        assertThat(commentLike.getContentModifiedAt()).isNull();
        assertThat(commentLike.getDeletedAt()).isNull();
    }

    @Nested
    class CommentLike_domain_features {

        @Test
        void can_set_status_soft_deleted() {
            var commentLike = CommentLike.builder()
                    .member(member)
                    .comment(comments[0])
                    .build();

            em.persist(commentLike);

            commentLike.softDelete();

            assertThat(commentLike.getStatus()).isEqualTo(EntityStatus.SOFT_DELETED);
        }
    }

    @Nested
    class Cannot_create_CommentLike {

        @Test
        void without_member() {
            var commentLike = CommentLike.builder()
                    .comment(comments[0])
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
            var commentLike1 = CommentLike.builder()
                    .member(member)
                    .comment(comments[0])
                    .build();
            var commentLike2 = CommentLike.builder()
                    .member(member)
                    .comment(comments[0])
                    .build();

            em.persist(commentLike1);
//        em.persist(commentLike2);

            log.info("{}", commentLike1.getId());
//        log.info("{}", commentLike2.getId());

            assertThatThrownBy(() -> em.persist(commentLike2));
            transaction.rollback();
        }
    }
}
