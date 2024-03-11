package com.vong.manidues.member.dto;

import com.vong.manidues.member.Member;
import com.vong.manidues.member.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class MemberRegisterRequest {

    @NotBlank(message = "Email 을 입력해주세요.")
    @Pattern(
            regexp =
            "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$"
    )
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Pattern(
            regexp =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$"
    )
    @Size(
            min = 8
            , max = 20
            , message = "비밀번호는 8 ~ 20 자리로 입력해주세요."
    )
    private String password;

    @NotBlank(message = "비밀번호 확인란을 입력해주세요.")
    private String passwordCheck;

    @NotBlank(message = "Nickname 을 입력해주세요.")
    private String nickname;

    public Member toEntity(String password) {
        return Member.builder()
                .email(this.email)
                .password(password)
                .nickname(this.nickname)
                .role(Role.USER)
                .build();
    }

}
