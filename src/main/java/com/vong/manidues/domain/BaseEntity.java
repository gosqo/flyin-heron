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
    @Column(nullable = false)
    protected LocalDateTime registeredAt;
    @LastModifiedDate
    @Column(nullable = false)
    protected LocalDateTime updatedAt;
    protected LocalDateTime contentModifiedAt;
    /**
     * 활성화된 엔티티는 deletedAt 컬럼의 값을 null 로 가짐.<br />
     * 소프트 삭제된 엔티티는 삭제한 시점의 LocalDateTime 을 deletedAt 컬럼의 값으로 가짐
     */
    protected LocalDateTime deletedAt;

    /**
     * 해당 객체를 상속하는 경우, 개체 status 초기화 변경 필요가 있다면 @Override 해야함.
     */
    @PrePersist
    protected void prePersist() {
        this.status = EntityStatus.ACTIVE;
    }

    protected Boolean isActive() {
        return getStatus().equals(EntityStatus.ACTIVE);
    }

    protected Boolean isSoftDeleted() {
        return getStatus().equals(EntityStatus.SOFT_DELETED);
    }

    public void activate() {
        updateStatus(EntityStatus.ACTIVE);
        updateDeletedAt(null); // 활성화된 엔티티는 deletedAt 컬럼의 값을 null 로 가짐. (정책)
    }

    public void softDelete() {
        updateStatus(EntityStatus.SOFT_DELETED);
        updateDeletedAtNow(); // 소프트 삭제된 엔티티는 삭제한 시점의 LocalDateTime 을 deletedAt 컬럼의 값으로 가짐. (정책)
    }

    protected void updateContentModifiedAt() {
        updateContentModifiedAt(LocalDateTime.now());
    }

    protected void updateDeletedAtNow() {
        updateDeletedAt(LocalDateTime.now());
    }

    protected void updateStatus(EntityStatus status) {
        this.status = status;
    }

    protected void updateContentModifiedAt(LocalDateTime dateTime) {
        this.contentModifiedAt = dateTime;
    }

    protected void updateDeletedAt(LocalDateTime dateTime) {
        this.deletedAt = dateTime;
    }
}
