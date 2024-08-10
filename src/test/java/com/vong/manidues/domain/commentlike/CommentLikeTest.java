package com.vong.manidues.domain.commentlike;

import com.vong.manidues.DataJpaTestEntityManagerBase;
import com.vong.manidues.domain.EntityStatus;
import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class CommentLikeTest extends DataJpaTestEntityManagerBase {

    @Autowired
    public CommentLikeTest(EntityManagerFactory emf) {
        super(emf);
    }

    @Test
    @DisplayName("Cannot create without member")
    void canNotCreateCommentLikeWithoutMember() {
        var commentLike = CommentLike.builder()
                .comment(comments[0])
                .build();

        assertThatThrownBy(() -> em.persist(commentLike));

        transaction.rollback();
    }

    @Test
    @DisplayName("Cannot create without comment")
    void canNotCreateCommentLikeWithoutComment() {
        var commentLike = CommentLike.builder()
                .member(member)
                .build();

        assertThatThrownBy(() -> em.persist(commentLike));

        transaction.rollback();
    }

    @Test
    @DisplayName("When create normally")
    void createCommentLikeNormally() {
        var targetComment = comments[0];
        var commentLike = CommentLike.builder()
                .member(member)
                .comment(targetComment)
                .build();

        em.persist(commentLike);

        assertThat(commentLike.getStatus()).isEqualTo(EntityStatus.ACTIVE);
        assertThat(commentLike.getMember()).isEqualTo(member);
        assertThat(commentLike.getComment()).isEqualTo(targetComment);

        assertThat(commentLike.getRegisteredAt()).isEqualTo(commentLike.getUpdatedAt());

        assertThat(commentLike.getContentModifiedAt()).isNull();
        assertThat(commentLike.getDeletedAt()).isNull();
    }
}
