package com.gosqo.flyinheron.domain;

import com.gosqo.flyinheron.global.config.JpaAuditingConfig;
import com.gosqo.flyinheron.global.data.TestDataInitializer;
import com.gosqo.flyinheron.global.data.TestDataRemover;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest(showSql = false)
@Import(value = { JpaAuditingConfig.class, TestDataRemover.class })
@Slf4j
public abstract class EntityManagerDataInitializer extends TestDataInitializer {
    protected final EntityManager em;
    protected final TestDataRemover remover;

    public EntityManagerDataInitializer(EntityManager em, TestDataRemover remover) {
        this.em = em;
        this.remover = remover;
    }

    protected void initMember() {
        member = buildMember();
        em.persist(member);
    }

    protected void initBoards() {
        initMember();
        boards = buildBoards();
        boards.forEach(em::persist);
    }

    protected void initComments() {
        initBoards();
        comments = buildComments();
        comments.forEach(em::persist);
    }

    protected void initCommentLikes() {
        initComments();
        commentLikes = buildCommentLikes();
        commentLikes.forEach(em::persist);
    }
}
