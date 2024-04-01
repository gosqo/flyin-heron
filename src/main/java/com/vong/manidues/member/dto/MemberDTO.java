package com.vong.manidues.member.dto;

import com.vong.manidues.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Builder
public class MemberDTO {

    private Long id;
    private String email;
    private String password;
    private String nickname;
    private LocalDateTime registerDate;

    public static MemberDTO fromEntity(Member member) {
        return MemberDTO.builder()
                .id(member.getId())
                .email(member.getEmail())
                .password(member.getPassword())
                .nickname(member.getNickname())
                .registerDate(member.getRegisterDate())
                .build();
    }
}
