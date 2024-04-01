package com.vong.manidues.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class IsPresentRequest {
    @NotBlank(message = "중복 확인할 값을 입력해주세요.")
    private String valueToCheck;
}
