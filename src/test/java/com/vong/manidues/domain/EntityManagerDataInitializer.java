package com.vong.manidues.domain;

import com.vong.manidues.global.config.JpaAuditingConfig;
import com.vong.manidues.global.data.TestDataInitializer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@Import(JpaAuditingConfig.class)
@Slf4j
abstract class EntityManagerDataInitializer extends TestDataInitializer {
    protected final EntityManager em;
    protected EntityTransaction transaction;

    @Autowired
    public EntityManagerDataInitializer(EntityManagerFactory emf) {
        this.em = emf.createEntityManager();
        this.transaction = em.getTransaction();
        transaction.begin();
    }

    @AfterEach
    void tearDown() {
        if (!transaction.isActive()) {
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

    @Override
    protected void initMember() {
        member = buildMember();
        em.persist(member);
    }

    @Override
    protected void initBoards() {
        initMember();
        boards = buildBoards();
        boards.forEach(em::persist);
    }

    @Override
    protected void initComments() {
        initBoards();
        comments = buildComments();
        comments.forEach(em::persist);
    }
}
