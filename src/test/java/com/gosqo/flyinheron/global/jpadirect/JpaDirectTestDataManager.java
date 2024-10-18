package com.gosqo.flyinheron.global.jpadirect;

import com.gosqo.flyinheron.global.data.TestDataInitializer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.metamodel.EntityType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;

@Slf4j
public abstract class JpaDirectTestDataManager extends TestDataInitializer {
    protected final EntityManager em;
    protected EntityTransaction transaction;

    public JpaDirectTestDataManager() {
        this.em = JpaDirect.getTestOnlyEntityManagerFactory().createEntityManager();

        this.transaction = em.getTransaction();
        transaction.begin();
    }

    @AfterEach
    void tearDown() {
        if (!transaction.isActive()) {
            transaction.begin();
        }

        log.info("==== Deleting test data. ====");

        var tables = em.getMetamodel().getEntities();
        var tableNames = tables.stream()
                .map(EntityType::getName)
                .map(entityName -> entityName
                        .replace("JpaEntity", "")
                        .replaceAll("([a-z])([A-Z])", "$1_$2")
                        .toLowerCase())
                .toList();

        em.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

        tableNames.forEach(tableName -> {
                    String query = String.format("TRUNCATE TABLE %s", tableName);
                    em.createNativeQuery(query).executeUpdate();
                }
        );

        em.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();

        transaction.commit();

        em.close();
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
