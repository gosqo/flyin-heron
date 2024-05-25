package com.vong.manidues.member;

import com.vong.manidues.member.dto.ChangeMemberPasswordRequest;
import com.vong.manidues.member.dto.MemberVerificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static com.vong.manidues.auth.AuthenticationFixture.MEMBER_EMAIL;
import static com.vong.manidues.auth.AuthenticationFixture.PASSWORD;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
public class MemberServiceTests {

    @Autowired
    private MemberService service;

    private String email = MEMBER_EMAIL;
    private String password = PASSWORD;
    private String changedPassword = PASSWORD + "1";

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
