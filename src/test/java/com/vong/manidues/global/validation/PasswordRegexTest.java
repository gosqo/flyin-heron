package com.vong.manidues.global.validation;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PasswordRegexTest {
    private final String passwordRegexRemovedRedundantEscape = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d|.*[!@#$%^&*()_+`~\\-=\\[\\]{}\\\\|;':\",./<>?₩])[A-Za-z\\d!@#$%^&*()_+`~\\-=\\[\\]{}\\\\|;':\",./<>?₩]{8,20}$";

    @Test
    void enum_regex_pass_cases() {
        System.out.println(SignUpValidation.PASSWORD.value());
        String[] items = {
                "paSsword!"
                , "paSsword()"
                , "pA!@#$%^&*()_+"
                , "pA-=`~[]{}\\|"
                , "pA|;':\",./<>?₩"
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
        System.out.println("==============");
    }

    @Test
    void enum_regex_fail_cases() {
        String[] items = {
                "password1"
                , "PASSWORD1"
                , "P@ssw0rd1234567890123" // 21 characters.
                , "12345678"
                , "*&^*&$^#&"
                , "  &*( &*( "
                , "asdf Asd f&7" // \s space
                , "ASDasADSsd"
        };

        for (String item : items) {
            boolean result = item.matches(SignUpValidation.PASSWORD.value());
            System.out.printf("%20s -> %b expected false.\n", item, result);
            assertThat(result).isFalse();
        }
        System.out.println("==============");
    }

    @Test
    void field_regex_pass_cases() {
        System.out.println(passwordRegexRemovedRedundantEscape);
        String[] items = {
                "paSsword!"
                , "paSsword()"
                , "pA!@#$%^&*()_+"
                , "pA-=`~[]{}\\|"
                , "pA|;':\",./<>?₩"
                , "Abc12345"
                , "StrongPwd2022!!"
                , "2PassWord"
                , "p2aSsWord@#"
                , "****pA****"
                , "asdfAsdf&7"
        };

        for (String item : items) {
            boolean result = item.matches(passwordRegexRemovedRedundantEscape);
            System.out.printf("%20s -> %b expected true.\n", item, result);
            assertThat(result).isTrue();
        }
        System.out.println("==============");
    }

    @Test
    void field_regex_fail_cases() {
        String[] items = {
                "password1"
                , "PASSWORD1"
                , "P@ssw0rd1234567890123" // 21 characters.
                , "12345678"
                , "*&^*&$^#&"
                , "  &*( &*( "
                , "asdf Asd f&7" // \s space
                , "ASDasADSsd"
        };

        for (String item : items) {
            boolean result = item.matches(passwordRegexRemovedRedundantEscape);
            System.out.printf("%20s -> %b expected false.\n", item, result);
            assertThat(result).isFalse();
        }
        System.out.println("==============");
    }
}
