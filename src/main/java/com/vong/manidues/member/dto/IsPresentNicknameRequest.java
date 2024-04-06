package com.vong.manidues.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class IsPresentNicknameRequest {
    // TODO 닉네임과 이메일 검증 기준이 다르기 때문에 각각을 분리해야함.
    @NotBlank(message = "중복 확인할 값을 입력해주세요.")
    private String valueToCheck;
}
