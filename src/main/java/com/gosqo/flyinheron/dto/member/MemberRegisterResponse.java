package com.gosqo.flyinheron.dto.member;

import lombok.Builder;

@Builder
public record MemberRegisterResponse(
        int status
        , String message
        , String accessToken
) {}
