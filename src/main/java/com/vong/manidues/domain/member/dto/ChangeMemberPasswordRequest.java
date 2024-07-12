package com.vong.manidues.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangeMemberPasswordRequest {
    private String email;

    @NotBlank(message = "기존 비밀번호를 입력해주세요.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d|.*[\\W\\S])[A-Za-z\\d|\\W\\S]{8,20}$"
            , message = "비밀번호는 8 ~ 20 자리, 영문 대소문자와 숫자 혹은 특수문자를 하나 이상 조합해주세요."
    )
    @Size(
            min = 8
            , max = 20
            , message = "비밀번호는 8 ~ 20 자리로 입력해주세요."
    )
    private String currentPassword;

    @NotBlank(message = "새 비밀번호를 입력해주세요.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d|.*[\\W\\S])[A-Za-z\\d|\\W\\S]{8,20}$"
            , message = "비밀번호는 8 ~ 20 자리, 영문 대소문자와 숫자 혹은 특수문자를 하나 이상 조합해주세요."
    )
    @Size(
            min = 8
            , max = 20
            , message = "비밀번호는 8 ~ 20 자리로 입력해주세요."
    )
    private String changedPassword;

    @NotBlank(message = "새 비밀번호 확인란을 입력해주세요.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d|.*[\\W\\S])[A-Za-z\\d|\\W\\S]{8,20}$"
            , message = "비밀번호는 8 ~ 20 자리, 영문 대소문자와 숫자 혹은 특수문자를 하나 이상 조합해주세요."
    )
    @Size(
            min = 8
            , max = 20
            , message = "비밀번호는 8 ~ 20 자리로 입력해주세요."
    )
    private String changedPasswordCheck;
}
