package com.gosqo.flyinheron.domain;

import com.gosqo.flyinheron.domain.member.Role;
import com.gosqo.flyinheron.dto.member.MemberLightInfo;
import com.gosqo.flyinheron.dto.memberprofileimage.MemberProfileImageLightInfo;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MemberModel {
    private final Long id;
    private final String email;
    private final String password;
    private final String nickname;
    private final LocalDateTime registerDate;
    private final Role role;

    private MemberProfileImage profileImage;

    @Builder
    public MemberModel(
            Long id,
            MemberProfileImage profileImage,
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

    public MemberLightInfo toLightInfo() {
        MemberProfileImageLightInfo profileImage = this.profileImage == null ? null : this.profileImage.toLightInfo();

        return MemberLightInfo.builder()
                .id(this.id)
                .profileImage(profileImage)
                .nickname(this.nickname)
                .registerDate(this.registerDate)
                .build();
    }
}
