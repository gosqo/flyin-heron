package com.gosqo.flyinheron.global.jpadirect;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

class JpaDirect {
    private static EntityManagerFactory entityManagerFactory;

    static EntityManagerFactory getTestOnlyEntityManagerFactory() {
        if (entityManagerFactory == null) {
            entityManagerFactory = Persistence.createEntityManagerFactory("testOnlyPersistenceUnit");
            return entityManagerFactory;
        }

        return entityManagerFactory;
    }
}
