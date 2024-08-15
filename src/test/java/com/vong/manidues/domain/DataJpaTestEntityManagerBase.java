package com.vong.manidues.domain;

import com.vong.manidues.domain.fixture.BoardFixture;
import com.vong.manidues.domain.member.Role;
import com.vong.manidues.global.config.JpaAuditingConfiguration;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@ActiveProfiles("test")
@Import(JpaAuditingConfiguration.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Slf4j
abstract class DataJpaTestEntityManagerBase {
    private static final int BOARD_COUNT = 3;
    private static final int COMMENT_COUNT = 3;

    private final EntityManagerFactory emf;
    protected EntityManager em;
    protected EntityTransaction transaction;

    protected Member member = buildMember();
    protected Board[] boards = buildBoards();
    protected Comment[] comments = buildComments();

    @Autowired
    public DataJpaTestEntityManagerBase(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @BeforeEach
    void setUp() {

        initializeData();
    }

    @AfterEach
    void tearDown() {
        if (!transaction.isActive()) {
            transaction = em.getTransaction();
            transaction.begin();
        }

        log.info("==== Deleting test data. ====");

        em.createQuery("DELETE FROM CommentLike cl").executeUpdate();
        em.createQuery("DELETE FROM Comment c").executeUpdate();
        em.createQuery("DELETE FROM Board b").executeUpdate();
        em.createQuery("DELETE FROM Member m").executeUpdate();

        var commentList = em.createQuery("SELECT c FROM Comment c").getResultList();
        var boardList = em.createQuery("SELECT b FROM Board b").getResultList();
        var memberList = em.createQuery("SELECT m FROM Member m").getResultList();

        assertThat(commentList).isEmpty();
        assertThat(boardList).isEmpty();
        assertThat(memberList).isEmpty();

        transaction.commit();
    }

    private Member buildMember() {
        return Member.builder()
                .email("check@auth.io")
                .nickname("testOnly")
                .password("$2a$10$.YTh5A02ylk3nhxMltZ0F.fdPp0InH6Sin.w91kve8SEGUYR4KAZ.")
                .role(Role.USER)
                .build();
    }

    private Board[] buildBoards() {
        Board[] boards = new Board[BOARD_COUNT];
        for (int i = 0; i < BOARD_COUNT; i++) {
            boards[i] = Board.builder()
                    .member(member)
                    .title(BoardFixture.TITLE + i + 1)
                    .content(BoardFixture.CONTENT + i + 1)
                    .build();
        }
        return boards;
    }

    private Comment[] buildComments() {
        Comment[] comments = new Comment[COMMENT_COUNT];
        IntStream.range(0, COMMENT_COUNT).forEach(i -> {
            comments[i] = Comment.builder()
                    .member(member)
                    .board(boards[0])
                    .content("comment goes " + i)
                    .build();
        });

        return comments;
    }

    private void initializeData() {
        List<Object> entityList = new ArrayList<>();
        entityList.add(member);
        entityList.addAll(Arrays.asList(boards));
        entityList.addAll(Arrays.asList(comments));

        saveAll(entityList);
    }

    private <T> void saveAll(List<Object> entities) {
        em = emf.createEntityManager();
        transaction = em.getTransaction();

        transaction.begin();

        entities.forEach(em::persist);
        transaction.commit();
        log.info("==== Test data initialized. ====");

        transaction.begin();
    }
}
