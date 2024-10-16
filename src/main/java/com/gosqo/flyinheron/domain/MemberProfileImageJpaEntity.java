package com.gosqo.flyinheron.domain;

import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
public class MemberProfileImageJpaEntity extends UuidBaseEntity {
    private Long memberId;
    private String originalFilename;
    private String fullPath;

    @Builder
    public MemberProfileImageJpaEntity(Long memberId, String originalFilename, String fullPath) {
        this.memberId = memberId;
        this.originalFilename = originalFilename;
        this.fullPath = fullPath;
    }
}
