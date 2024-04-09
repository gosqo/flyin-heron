package com.vong.manidues.member;

import com.vong.manidues.member.dto.ChangeMemberPasswordRequest;
import com.vong.manidues.member.dto.MemberVerificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class MemberServiceTests {

    @Autowired
    private MemberService service;

    @Value("${test.variable.email}")
    private String email;
    @Value("${test.variable.password}")
    private String password;
    @Value("${test.variable.changed-password}")
    private String changedPassword;

    @Test
    public void memberVerification() {
        MemberVerificationRequest request =
                MemberVerificationRequest.builder()
                        .email(email)
                        .password(password)
                        .build();
        Assertions.assertTrue(service.verifyOneself(request));
    }

    @Test
    public void memberChangePassword() {
        ChangeMemberPasswordRequest request =
                ChangeMemberPasswordRequest.builder()
                        .email(email)
                        .currentPassword(password)
                        .changedPassword(changedPassword)
                        .changedPasswordCheck(changedPassword)
                        .build();
        Assertions.assertTrue(service.changePassword(request));
    }
}
