package com.vong.manidues.member.dto;

import com.vong.manidues.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class MemberRegisterRequest {

    private String email;
    private String password;
    private String nickname;
    private LocalDateTime registerDate;

    public Member toEntity(String password) {
        return Member.builder()
                .email(this.email)
                .password(password)
                .nickname(this.nickname)
                .registerDate(this.registerDate)
                .build();
    }

}
