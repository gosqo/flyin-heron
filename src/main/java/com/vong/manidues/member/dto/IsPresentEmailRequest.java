package com.vong.manidues.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class IsPresentEmailRequest {
    @NotBlank(message = "중복 확인할 값을 입력해주세요.")
    @Pattern(
            regexp = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*[0-9a-zA-Z]+.[a-zA-Z]{2,3}$",
            message = "올바른 형식의 영문 Email 을 입력해주세요."
    )
    @Size(
            min = 6
            , max = 50
            , message = "이메일은 6 ~ 50 자리로 입력해주세요."
    )
    private String email;
}
