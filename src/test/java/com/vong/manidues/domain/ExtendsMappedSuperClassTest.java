package com.vong.manidues.domain;

import com.vong.manidues.DataJpaTestEntityManagerBase;
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
        return "TestEntity{" +
                "field1='" + field1 + '\'' +
                ", field2=" + field2 +
                ", field3=" + field3 +
                ", id=" + id +
                ", status=" + status +
                ", registeredAt=" + registeredAt +
                ", updatedAt=" + updatedAt +
                ", contentModifiedAt=" + contentModifiedAt +
                ", deletedAt=" + deletedAt +
                '}';
    }
}

@Slf4j
public class ExtendsMappedSuperClassTest extends DataJpaTestEntityManagerBase {
    @Autowired
    public ExtendsMappedSuperClassTest(EntityManagerFactory emf) {
        super(emf);
    }

    @Test
    void createExtendedTestEntity() {
        TestEntity testEntity = new TestEntity("hello", 1L, true);

        em.persist(testEntity);
        log.info("{}", testEntity.toString());


        assertThat(testEntity.getId()).isEqualTo(1L);
        assertThat(testEntity.getStatus()).isEqualTo(EntityStatus.ACTIVE);

        assertThat(testEntity.getRegisteredAt()).isNotNull();
        assertThat(testEntity.getUpdatedAt()).isNotNull();
        assertThat(testEntity.getRegisteredAt()).isEqualTo(testEntity.getUpdatedAt());

        assertThat(testEntity.getDeletedAt()).isNull();
        assertThat(testEntity.getContentModifiedAt()).isNull();
    }
}
