package com.gosqo.flyinheron.global.validation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@Slf4j
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class EmailRegexpTest {

    @Test
    void case_not_matches_email_regex() {
//        String regExp = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*[.][a-zA-Z]{2,3}$";
        String[] InappropriateEmails = {
                "helllo@world..cc" // 마지막 . 이전에 영문 대소문자 혹은 숫자가 존재하지 않음.
                , "hello@world00.commm" // 최상위 도메인 자리수 초과
                , "hello@world.d09.c" // 최상위 도메인 자리수 미달
                , "hello@world--asd_asd.d09.co" // 연속된 하이픈
                , "hello@world-_._..-asd_asd.d09.com" // 허용 특수문자의 연속된 사용
                , "hello@world*asd.com" // 부적절 특수문자 포함
        };
        for (String email : InappropriateEmails) {
            log.info(email.matches(SignUpValidation.EMAIL.value()) + " " + email);
            Assertions.assertFalse(email.matches(SignUpValidation.EMAIL.value()), email);
        }
    }

    @Test
    void case_matches_email_regex() {
//        String regExp = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*[.][a-zA-Z]{2,3}$";
        String[] InappropriateEmails = {
                "helllo@world.cc"
                , "hello@world00.com"
                , "hello00@world00.com"
                , "hello.00-asd_dasd@world.00-asd_asd9.com"
                , "hello@world0-asd_asd.asd0.com"
                , "hello@world.d09.co"
                , "hello@world-asd_asd.d09.cac"
        };
        for (String email : InappropriateEmails) {
            log.info(email.matches(SignUpValidation.EMAIL.value()) + " " + email);
            Assertions.assertTrue(email.matches(SignUpValidation.EMAIL.value()), email);
        }
    }
}
