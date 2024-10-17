package com.gosqo.flyinheron.global.data;

import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.EntityType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class TestDataRemover {
    private final EntityManager em;
    private final JdbcTemplate jdbcTemplate;

    public TestDataRemover(EntityManager em, JdbcTemplate jdbcTemplate) {
        this.em = em;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void removeAll() {
        var tables = em.getMetamodel().getEntities();
        var tableNames = tables.stream()
                .map(EntityType::getName)
                .map(entityName -> entityName
                        .replace("JpaEntity", "")
                        .replaceAll("([a-z])([A-Z])", "$1_$2")
                        .toLowerCase())
                .toList();

        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");

        tableNames.forEach(tableName -> {
                    String query = String.format("TRUNCATE TABLE %s", tableName);
                    jdbcTemplate.execute(query);
                }
        );

        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");
    }
}
