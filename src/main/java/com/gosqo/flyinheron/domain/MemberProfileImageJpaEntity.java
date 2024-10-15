package com.gosqo.flyinheron.domain;

import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
public class MemberProfileImageJpaEntity extends UuidBaseEntity {
    private Long memberId;
    private String originalFilename;
    private String fileFullPath;

    @Builder
    public MemberProfileImageJpaEntity(Long memberId, String originalFilename, String fileFullPath) {
        this.memberId = memberId;
        this.originalFilename = originalFilename;
        this.fileFullPath = fileFullPath;
    }
}
