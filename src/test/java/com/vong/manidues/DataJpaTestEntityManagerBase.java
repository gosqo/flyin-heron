package com.vong.manidues;

import com.vong.manidues.domain.board.Board;
import com.vong.manidues.domain.board.BoardFixture;
import com.vong.manidues.domain.member.Member;
import com.vong.manidues.domain.member.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Slf4j
public class DataJpaTestEntityManagerBase {
    private final EntityManagerFactory emf;
    protected EntityManager em;
    protected EntityTransaction transaction;
    protected Member member = Member.builder()
            .email("check@auth.io")
            .nickname("testOnly")
            .password("$2a$10$.YTh5A02ylk3nhxMltZ0F.fdPp0InH6Sin.w91kve8SEGUYR4KAZ.")
            .role(Role.USER)
            .build();
    private static final int BOARD_COUNT = 3;
    protected Board[] boards = buildBoards();

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

    private void initializeData() {
        List<Object> entityList = new ArrayList<>();
        entityList.add(member);
        entityList.addAll(Arrays.asList(boards));

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
