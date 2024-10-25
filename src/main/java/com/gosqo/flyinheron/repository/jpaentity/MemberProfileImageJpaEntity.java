package com.gosqo.flyinheron.repository.jpaentity;

import com.gosqo.flyinheron.domain.Member;
import com.gosqo.flyinheron.domain.UuidBaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "member_profile_image")
@NoArgsConstructor
public class MemberProfileImageJpaEntity extends UuidBaseEntity {

    @OneToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String originalFilename;

    @Column(nullable = false)
    private String renamedFilename;

    @Column(nullable = false)
    private String referencePath;

    @Column(nullable = false)
    private String fullPath;

    @Builder
    public MemberProfileImageJpaEntity(
            Member member
            , String originalFilename
            , String renamedFilename
            , String referencePath
            , String fullPath) {
        this.member = member;
        this.originalFilename = originalFilename;
        this.renamedFilename = renamedFilename;
        this.referencePath = referencePath;
        this.fullPath = fullPath;
    }

    public void updateMember(Member member) {
        this.member = member;

        if (this.member.getProfileImage() != this) {
            this.member.updateProfileImage(this);
        }
    }

    public void updateImage(MemberProfileImageJpaEntity updateEntity) {
        this.originalFilename = updateEntity.getOriginalFilename();
        this.renamedFilename = updateEntity.getRenamedFilename();
        this.referencePath = updateEntity.getReferencePath();
        this.fullPath = updateEntity.getFullPath();
    }
}
