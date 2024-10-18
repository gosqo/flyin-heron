package com.gosqo.flyinheron.global.jpadirect;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JpaDirect {
    private static EntityManagerFactory entityManagerFactory;

    public static EntityManagerFactory getTestOnlyEntityManagerFactory() {
        if (entityManagerFactory == null) {
            entityManagerFactory = Persistence.createEntityManagerFactory("testOnlyPersistenceUnit");
            return entityManagerFactory;
        }

        return entityManagerFactory;
    }

    public static EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    public static void closeEntityManagerFactory() {
        entityManagerFactory.close();
    }
}
