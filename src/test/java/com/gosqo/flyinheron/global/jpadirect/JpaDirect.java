package com.gosqo.flyinheron.global.jpadirect;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JpaDirect {
    private static final EntityManagerFactory entityManagerFactory =
            Persistence.createEntityManagerFactory("testOnlyPersistenceUnit");

    public static EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    public static void closeEntityManagerFactory() {
        entityManagerFactory.close();
    }
}
