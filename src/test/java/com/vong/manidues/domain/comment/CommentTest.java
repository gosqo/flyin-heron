package com.vong.manidues.domain.comment;

import com.vong.manidues.DataJpaTestEntityManagerBase;
import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@ActiveProfiles("test")
public class CommentTest extends DataJpaTestEntityManagerBase {
    
    @Autowired
    public CommentTest(EntityManagerFactory emf) {
        super(emf);
    }

    @Test
    void can_not_create_without_content() {
        Comment comment = Comment.builder()
                .member(member)
                .board(boards[0])
                .build();

        assertThatThrownBy(() -> em.persist(comment));
        transaction.rollback();
    }

    @Test
    void can_not_create_without_member() {
        Comment comment = Comment.builder()
                .content("hello comment")
                .board(boards[0])
                .build();

        assertThatThrownBy(() -> em.persist(comment));
        transaction.rollback();
    }

    @Test
    void can_not_create_without_board() {
        Comment comment = Comment.builder()
                .content("hello comment")
                .member(member)
                .build();

        assertThatThrownBy(() -> em.persist(comment));
        transaction.rollback();
    }

    @Test
    void saveComment() {
        Comment storing = Comment.builder()
                .content("Hello, comment")
                .member(member)
                .board(boards[0])
                .build();
        em.persist(storing);
        em.flush();

        Comment stored = em.find(Comment.class, storing.getId());
        em.refresh(stored);

        assertThat(stored.getMember().getId()).isEqualTo(storing.getMember().getId());
        assertThat(stored.getBoard().getId()).isEqualTo(storing.getBoard().getId());
        assertThat(stored.getMember()).isEqualTo(storing.getMember());
        assertThat(stored.getBoard()).isEqualTo(storing.getBoard());
    }
}
