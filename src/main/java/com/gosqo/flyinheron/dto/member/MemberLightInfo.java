package com.gosqo.flyinheron.dto.member;

import com.gosqo.flyinheron.dto.memberprofileimage.MemberProfileImageLightInfo;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MemberLightInfo(
        Long id
        , MemberProfileImageLightInfo profileImage
        , String nickname
        , LocalDateTime registerDate
) {
}
