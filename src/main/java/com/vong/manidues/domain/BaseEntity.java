package com.vong.manidues.domain;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    @Enumerated(value = EnumType.STRING)
    protected EntityStatus status;

    @CreatedDate
    protected LocalDateTime registeredAt;
    @LastModifiedDate
    protected LocalDateTime updatedAt;
    protected LocalDateTime contentModifiedAt;
    protected LocalDateTime deletedAt;

    /**
     * 해당 객체를 상속하는 경우, 개체 활성화 승인 여부에 따른 status 필드를 초기화하는 메서드 구현.
     */
    @PrePersist
    protected abstract void prePersist();

    protected void updateStatusActive() {
        updateStatus(EntityStatus.ACTIVE);
    }

    protected void updateStatus(EntityStatus status) {
        this.status = status;
    }

    protected void updateContentModifiedAt(LocalDateTime dateTime) {
        this.contentModifiedAt = dateTime;
    }

    protected void updateContentModifiedAt() {
        updateContentModifiedAt(LocalDateTime.now());
    }

    protected Boolean isSoftDeleted() {
        return getStatus() == EntityStatus.SOFT_DELETED;
    }

    protected void softDelete() {
        updateDeletedAt();
        updateStatus(EntityStatus.SOFT_DELETED);
    }

    protected void updateDeletedAt(LocalDateTime dateTime) {
        this.deletedAt = dateTime;
    }

    protected void updateDeletedAt() {
        updateDeletedAt(LocalDateTime.now());
    }
}
