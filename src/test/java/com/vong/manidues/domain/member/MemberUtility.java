package com.vong.manidues.domain.member;

import static com.vong.manidues.domain.member.MemberFixture.*;
import static com.vong.manidues.domain.member.Role.USER;

public class MemberUtility {
    public static Member buildMockMember() {
        return Member.builder()
                .nickname(NICKNAME)
                .password(PASSWORD)
                .email(EMAIL)
                .role(USER)
                .id(1L)
                .build();
    }
}
