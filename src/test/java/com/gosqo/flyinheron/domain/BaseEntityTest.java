package com.gosqo.flyinheron.domain;

import com.gosqo.flyinheron.global.jpadirect.JpaDirectTestDataManager;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class BaseEntityTest extends JpaDirectTestDataManager {

    private TestEntity newTestEntity() {
        return new TestEntity("This is a TestEntity.", 999L, true);
    }

    @Test
    void can_persist_TestEntity_with_fields_in_BaseEntity() {
        TestEntity testEntity = newTestEntity();

        em.persist(testEntity);

        assertThat(testEntity.getId()).isNotNull();
        assertThat(testEntity.getStatus()).isEqualTo(EntityStatus.ACTIVE);
        assertThat(testEntity.isActive()).isTrue();

        assertThat(testEntity.getRegisteredAt()).isNotNull();
        assertThat(testEntity.getRegisteredAt()).isEqualTo(testEntity.getUpdatedAt());

        assertThat(testEntity.getDeletedAt()).isNull();
        assertThat(testEntity.getContentModifiedAt()).isNull();
    }

    @Test
    void can_set_status_soft_deleted() {
        var testEntity = newTestEntity();

        em.persist(testEntity);

        testEntity.softDelete();

        em.flush(); // BaseEntity @PreUpdate 구동: 데이터베이스에 업데이트 쿼리

        var found = em.find(TestEntity.class, testEntity.getId());

        assertThat(found.getStatus()).isEqualTo(EntityStatus.SOFT_DELETED);
        assertThat(found.isSoftDeleted()).isTrue();
        assertThat(testEntity.getRegisteredAt()).isNotEqualTo(testEntity.getUpdatedAt());
    }

    @Test
    void once_persisted_can_toggle_status_of_entity() {
        var testEntity = newTestEntity();

        em.persist(testEntity); // 저장
        em.flush();

        var found = em.find(TestEntity.class, testEntity.getId());
        assertThat(found.getDeletedAt()).isNull();

        found.softDelete(); // 소프트 삭제
        em.flush();

        var softDeletedEntity = em.find(TestEntity.class, found.getId());

        assertThat(softDeletedEntity.getDeletedAt()).isNotNull();
        assertThat(softDeletedEntity.getStatus()).isEqualTo(EntityStatus.SOFT_DELETED);
        assertThat(softDeletedEntity.isSoftDeleted()).isTrue();
        assertThat(softDeletedEntity.getUpdatedAt()).isNotEqualTo(softDeletedEntity.getRegisteredAt());

        softDeletedEntity.activate(); // 소프트 삭제된 엔티티 재활성화

        var activatedEntity = em.find(TestEntity.class, softDeletedEntity.getId());

        assertThat(activatedEntity.getStatus()).isEqualTo(EntityStatus.ACTIVE);
        assertThat(activatedEntity.isActive()).isTrue();
        assertThat(activatedEntity.isSoftDeleted()).isFalse(); // 위 코드라인이 수행하는 불리언 값
        assertThat(activatedEntity.getDeletedAt()).isNull();
    }
}

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
class TestEntity extends IdentityBaseEntity {
    private String field1;
    private Long field2;
    @Column(nullable = false)
    private Boolean field3;
}
