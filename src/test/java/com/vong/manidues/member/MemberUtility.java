package com.vong.manidues.member;

import static com.vong.manidues.auth.AuthenticationFixture.*;
import static com.vong.manidues.member.Role.USER;

public class MemberUtility {
    public static Member buildMockMember() {
        return Member.builder()
                .nickname(NICKNAME)
                .password(PASSWORD)
                .email(MEMBER_EMAIL)
                .role(USER)
                .id(1L)
                .build();
    }
}
