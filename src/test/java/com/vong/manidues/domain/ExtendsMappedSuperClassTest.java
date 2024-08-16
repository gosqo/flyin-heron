package com.vong.manidues.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManagerFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@Nested
@Getter
@AllArgsConstructor
@Entity
class TestEntity extends IdentityBaseEntity {
    private String field1;
    private Long field2;
    @Column(nullable = false)
    private Boolean field3;

    protected void prePersist() {
        super.status = EntityStatus.ACTIVE;
    }

    @Override
    public String toString() {
        return "\nTestEntity{" +
                "\n\tfield1='" + field1 + '\'' +
                "\n\t, field2=" + field2 +
                "\n\t, field3=" + field3 +
                "\n\t, id=" + id +
                "\n\t, status=" + status +
                "\n\t, registeredAt=" + registeredAt +
                "\n\t, updatedAt=" + updatedAt +
                "\n\t, contentModifiedAt=" + contentModifiedAt +
                "\n\t, deletedAt=" + deletedAt +
                "\n}";
    }
}

@Slf4j
class ExtendsMappedSuperClassTest extends EntityManagerDataInitializer {

    @Autowired
    public ExtendsMappedSuperClassTest(EntityManagerFactory emf) {
        super(emf);
    }

    @Test
    void sub_class_can_have_super_class_fields() {
        TestEntity testEntity = new TestEntity("hello", 1L, true);

        em.persist(testEntity);

        log.info("{}", testEntity);

        assertThat(testEntity.getId()).isNotNull();
        assertThat(testEntity.getStatus()).isEqualTo(EntityStatus.ACTIVE);

        assertThat(testEntity.getRegisteredAt()).isNotNull();
        assertThat(testEntity.getUpdatedAt()).isNotNull();
        assertThat(testEntity.getRegisteredAt()).isEqualTo(testEntity.getUpdatedAt());

        assertThat(testEntity.getDeletedAt()).isNull();
        assertThat(testEntity.getContentModifiedAt()).isNull();
    }
}
