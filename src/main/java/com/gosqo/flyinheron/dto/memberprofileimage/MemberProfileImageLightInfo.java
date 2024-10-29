package com.gosqo.flyinheron.dto.memberprofileimage;

import lombok.Builder;

@Builder
public record MemberProfileImageLightInfo(
        String referencePath
        , String originalFilename
        , String renamedFilename
) {
}
