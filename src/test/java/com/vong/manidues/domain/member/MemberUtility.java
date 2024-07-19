package com.vong.manidues.domain.member;

import com.vong.manidues.domain.member.Member;

import static com.vong.manidues.domain.auth.AuthenticationFixture.*;
import static com.vong.manidues.domain.member.Role.USER;

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
