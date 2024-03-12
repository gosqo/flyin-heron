package com.vong.manidues;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        // 비밀번호 유효성을 검사할 정규표현식
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[~!@#$%^&*()+{}|:\"<>?`=\\[\\]\\-_\\\\;',./])[A-Za-z\\d~!@#$%^&*()+{}|:\"<>?`=\\[\\]\\-_\\\\;',./]{8,20}$";

        // 테스트할 비밀번호
        String password = "Test123!@#'\\-_.";

        // 패턴 객체 생성
        Pattern pattern = Pattern.compile(passwordRegex);

        // 패턴 객체를 사용하여 비밀번호 검사
        Matcher matcher = pattern.matcher(password);

        // 비밀번호가 정규표현식과 일치하는지 확인
        if (matcher.matches()) {
            System.out.println("비밀번호가 유효합니다. " + password);
        } else {
            System.out.println("비밀번호가 유효하지 않습니다. " + password);
        }
    }
}
