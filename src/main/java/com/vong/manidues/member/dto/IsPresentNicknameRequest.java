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
public class IsPresentNicknameRequest {
    @NotBlank(message = "중복 확인할 값을 입력해주세요.")
    @Size(
            min = 2
            , max = 20
            , message = "닉네임은 2 ~ 20 자리로 입력해주세요."
    )
    @Pattern(
            regexp = "^[A-Za-z가-힣\\d-_./]{2,20}$",
            message = "닉네임은 2 ~ 20 자리, 영/한문과 특수문자{'-', '_', '.'} 을 사용해 구성할 수 있습니다."
    )
    private String nickname;
}
