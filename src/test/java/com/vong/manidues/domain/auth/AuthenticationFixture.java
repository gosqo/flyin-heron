package com.vong.manidues.domain.auth;

import com.vong.manidues.domain.auth.AuthenticationRequest;
import com.vong.manidues.domain.member.Member;
import com.vong.manidues.domain.member.Role;
import com.vong.manidues.domain.member.dto.MemberRegisterRequest;

public class AuthenticationFixture {
    public static final String MEMBER_EMAIL = "check@auth.io";
    public static final String PASSWORD = "Password0";
    public static final String ENTITY_PASSWORD = "$2a$10$.YTh5A02ylk3nhxMltZ0F.fdPp0InH6Sin.w91kve8SEGUYR4KAZ.";
    public static final String NICKNAME = "testOnly";
    public static final long MEMBER_ID = 1L;
    public static final Member MEMBER_ENTITY = Member.builder()
            .id(MEMBER_ID)
            .email(MEMBER_EMAIL)
            .password(ENTITY_PASSWORD)
            .nickname(NICKNAME)
            .role(Role.USER)
            .tokens(null)
            .boards(null)
            .build();
    public static final AuthenticationRequest AUTH_REQUEST = new AuthenticationRequest(MEMBER_EMAIL, PASSWORD);
    public static final MemberRegisterRequest MEMBER_REGISTER_REQUEST = MemberRegisterRequest.builder()
            .email(MEMBER_EMAIL)
            .password(PASSWORD)
            .passwordCheck(PASSWORD)
            .nickname(NICKNAME)
            .build();
}
