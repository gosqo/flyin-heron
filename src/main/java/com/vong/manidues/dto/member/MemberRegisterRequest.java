package com.vong.manidues.dto.member;

import com.vong.manidues.domain.Member;
import com.vong.manidues.domain.member.Role;
import jakarta.validation.constraints.*;
import lombok.*;

import static com.vong.manidues.global.validation.SignUpValidation.PASSWORD;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class MemberRegisterRequest {
    private final String PASSWORD_REGEX = PASSWORD.value();

    @NotBlank(message = "Email 을 입력해주세요.")
    @Email(
            regexp = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*[.][a-zA-Z]{2,3}$",
            message = "올바른 형식의 영문 Email 을 입력해주세요."
    )
    @Size(
            min = 6
            , max = 50
            , message = "이메일은 6 ~ 50 자리로 입력해주세요."
    )
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d|.*[!@#$%^&*()_+`~\\-=\\[\\]{}\\\\|;':\",./<>?₩])[A-Za-z\\d!@#$%^&*()_+`~\\-=\\[\\]{}\\\\|;':\",./<>?₩]{8,20}$"
            , message = "비밀번호는 8 ~ 20 자리, 영문 대소문자와 숫자 혹은 특수문자를 하나 이상 조합해주세요."
    )
    @Size(
            min = 8
            , max = 20
            , message = "비밀번호는 8 ~ 20 자리로 입력해주세요."
    )
    private String password;

    @NotBlank(message = "비밀번호 확인란을 입력해주세요.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d|.*[!@#$%^&*()_+`~\\-=\\[\\]{}\\\\|;':\",./<>?₩])[A-Za-z\\d!@#$%^&*()_+`~\\-=\\[\\]{}\\\\|;':\",./<>?₩]{8,20}$"
            , message = "비밀번호는 8 ~ 20 자리, 영문 대소문자와 숫자 혹은 특수문자를 하나 이상 조합해주세요."
    )
    @Size(
            min = 8
            , max = 20
            , message = "비밀번호는 8 ~ 20 자리로 입력해주세요."
    )
    private String passwordCheck;

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(
            min = 2
            , max = 20
            , message = "닉네임은 2 ~ 20 자리로 입력해주세요."
    )
    @Pattern(
            regexp = "^[0-9A-Za-z가-힣-_.]{2,20}$",
            message = "닉네임은 2 ~ 20 자리, 숫자, 영/한문과 특수문자{'-', '_', '.'} 을 사용해 구성할 수 있습니다."
    )
    private String nickname;

    @AssertTrue(message = "비밀번호가 일치하지 않습니다.")
    public boolean isPasswordMatch() {
        String password = this.getPassword();
        String passwordCheck = this.getPasswordCheck();
        return password != null
                && password.equals(passwordCheck);
    }

    public Member toEntity(String password) {
        return Member.builder()
                .email(this.email)
                .password(password)
                .nickname(this.nickname)
                .role(Role.USER)
                .build();
    }

}
