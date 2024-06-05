package com.vong.manidues.validation;

public enum SignUpValidation {
    EMAIL(
            "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*[.][a-zA-Z]{2,3}$"
            , "올바른 형식의 영문 Email 을 입력해주세요."
    )
    , NICKNAME(
            "^[A-Za-z가-힣\\d-_./]{2,20}$"
            , "닉네임은 2 ~ 20 자리, 숫자, 영/한문과 특수문자{'-', '_', '.'} 을 사용해 구성할 수 있습니다."
    )
    , PASSWORD(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d|.*[\\W\\S])[A-Za-z\\d|\\W\\S]{8,20}$"
            , "비밀번호는 8 ~ 20 자리, 영문 대소문자와 숫자 혹은 특수문자를 하나 이상 조합해주세요."
    );

    private static final SignUpValidation[] VALUES;

    static {
        VALUES = values();
    }

    private final String value;
    private final String message;

    SignUpValidation(String value, String message) {
        this.value = value;
        this.message = message;
    }

    public String value() {
        return this.value;
    }

    public String message() {
        return this.message;
    }
}
