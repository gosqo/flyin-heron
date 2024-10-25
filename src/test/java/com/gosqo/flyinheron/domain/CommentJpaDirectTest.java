package com.gosqo.flyinheron.domain;

import com.gosqo.flyinheron.global.jpadirect.JpaDirectTestDataManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
class CommentJpaDirectTest extends JpaDirectTestDataManager {
    private static final String COMMENT_CONTENT = "hello, comment.";

    @BeforeEach
    void setUp() {
        initBoards();
    }

    @Test
    void likeCount_cannot_be_negative_number() {
        Comment comment = Comment.builder()
                .member(member)
                .board(boards.get(0))
                .content(COMMENT_CONTENT)
                .build();

        em.persist(comment);
        assertThat(comment.getLikeCount()).isEqualTo(0L);

        comment.subtractLikeCount();

        assertThat(comment.getLikeCount()).isEqualTo(0L);
    }

    @Test
    void can_not_create_without_content() {
        Comment comment = Comment.builder()
                .member(member)
                .board(boards.get(0))
                .build();

        assertThatThrownBy(() -> em.persist(comment));
        transaction.rollback();
    }

    @Test
    void can_not_create_without_member() {
        Comment comment = Comment.builder()
                .content(COMMENT_CONTENT)
                .board(boards.get(0))
                .build();

        assertThatThrownBy(() -> em.persist(comment));
        transaction.rollback();
    }

    @Test
    void can_not_create_without_board() {
        Comment comment = Comment.builder()
                .content(COMMENT_CONTENT)
                .member(member)
                .build();

        assertThatThrownBy(() -> em.persist(comment));
        transaction.rollback();
    }

    @Test
    void saveComment() {
        Comment storing = Comment.builder()
                .content(COMMENT_CONTENT)
                .member(member)
                .board(boards.get(0))
                .build();
        em.persist(storing);
        em.flush();

        Comment stored = em.find(Comment.class, storing.getId());
        em.refresh(stored);

        assertThat(stored.getMember().getId()).isEqualTo(storing.getMember().getId());
        assertThat(stored.getBoard().getId()).isEqualTo(storing.getBoard().getId());
        assertThat(stored.getMember()).isEqualTo(storing.getMember());
        assertThat(stored.getBoard()).isEqualTo(storing.getBoard());
        assertThat(stored.getLikeCount()).isEqualTo(0L);

        // assertThat(stored.getRegisterDate()).isEqualTo(stored.getUpdateDate()); // 각각의 필드가 초기화되는 시점의 타임스탬프가 할당되어 서로 다름.
    }
}
