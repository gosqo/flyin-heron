package com.vong.manidues.domain.member;

import com.vong.manidues.domain.auth.AuthenticationRequest;
import com.vong.manidues.domain.member.dto.MemberRegisterRequest;

public class MemberFixture {
    public static final String EMAIL = "check@auth.io";
    public static final String PASSWORD = "Password0";
    public static final String ENCODED_PASSWORD = "$2a$10$.YTh5A02ylk3nhxMltZ0F.fdPp0InH6Sin.w91kve8SEGUYR4KAZ.";
    public static final String NICKNAME = "testOnly";
    public static final Member MEMBER = Member.builder()
            .email(EMAIL)
            .password(ENCODED_PASSWORD)
            .nickname(NICKNAME)
            .role(Role.USER)
            .build();
    public static final AuthenticationRequest AUTH_REQUEST = AuthenticationRequest.builder()
            .email(EMAIL)
            .password(PASSWORD)
            .build();
    public static final MemberRegisterRequest MEMBER_REGISTER_REQUEST = MemberRegisterRequest.builder()
            .email(EMAIL)
            .password(PASSWORD)
            .passwordCheck(PASSWORD)
            .nickname(NICKNAME)
            .build();
}
