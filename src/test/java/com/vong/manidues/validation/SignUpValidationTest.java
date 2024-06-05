package com.vong.manidues.validation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SignUpValidationTest {

    @Test
    void passPasswordRegex() {
        System.out.println(SignUpValidation.PASSWORD.value());
        String[] items = {
                "paSsword!"
                , "Abc12345"
                , "StrongPwd2022!!"
                , "2PassWord"
                , "p2aSsWord@#"
                , "****pA****"
                , "asdfAsdf&7"
        };

        for (String item : items) {
            boolean result = item.matches(SignUpValidation.PASSWORD.value());
            System.out.printf("%20s -> %b expected true.\n", item, result);
            assertThat(result).isTrue();
        }

    }

    @Test
    void failPasswordRegex() {
        String[] items = {
                "password1"
                , "PASSWORD1"
                , "P@ssw0rd1234567890123"
                , "12345678"
                , "*&^*&$^#&"
                , "  &*( &*( "
        };

        for (String item : items) {
            boolean result = item.matches(SignUpValidation.PASSWORD.value());
            System.out.printf("%20s -> %b expected false.\n", item, result);
            assertThat(result).isFalse();
        }
    }
}
