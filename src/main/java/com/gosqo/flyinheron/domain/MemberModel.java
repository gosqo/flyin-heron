package com.gosqo.flyinheron.domain;

import com.gosqo.flyinheron.domain.member.Role;
import com.gosqo.flyinheron.repository.jpaentity.MemberProfileImageJpaEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MemberModel {
    private Long id;
    private MemberProfileImageJpaEntity profileImage;
    private String email;
    private String password;
    private String nickname;
    private LocalDateTime registerDate;
    private Role role;

    @Builder
    public MemberModel(
            Long id,
            MemberProfileImageJpaEntity profileImage,
            String email,
            String password,
            String nickname,
            LocalDateTime registerDate,
            Role role
    ) {
        this.id = id;
        this.profileImage = profileImage;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.registerDate = registerDate;
        this.role = role;
    }
}
