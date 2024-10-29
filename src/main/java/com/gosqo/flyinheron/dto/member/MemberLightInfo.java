package com.gosqo.flyinheron.dto.member;

import com.gosqo.flyinheron.domain.MemberModel;
import com.gosqo.flyinheron.domain.MemberProfileImage;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MemberLightInfo {
    private Long id;
    private MemberProfileImage profileImage;
    private String nickname;
    private LocalDateTime registerDate;

    public static MemberLightInfo of(MemberModel model) {
        MemberLightInfo info = new MemberLightInfo();

        info.id = model.getId();
        info.profileImage = model.getProfileImage();
        info.nickname = model.getNickname();
        info.registerDate = model.getRegisterDate();

        return info;
    }
}
