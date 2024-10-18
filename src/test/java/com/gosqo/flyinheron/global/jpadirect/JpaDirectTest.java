package com.gosqo.flyinheron.global.jpadirect;

import com.gosqo.flyinheron.domain.Member;
import com.gosqo.flyinheron.domain.member.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.metamodel.EntityType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static com.gosqo.flyinheron.domain.fixture.MemberFixture.*;

@Slf4j
public class JpaDirectTest {
    protected final EntityManager em;
    protected EntityTransaction transaction;

    public JpaDirectTest() {
        this.em = JpaDirect.getEntityManager();
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
    }

    protected Member buildMember() {
        return Member.builder()
                .email(EMAIL)
                .nickname(NICKNAME)
                .password(ENCODED_PASSWORD)
                .role(Role.USER)
                .build();
    }

    @Test
    void hello_direct_Jpa() {
        Member member = buildMember();
        em.persist(member);
    }
}
