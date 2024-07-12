package com.vong.manidues.domain.member;

import com.vong.manidues.domain.member.MemberService;
import com.vong.manidues.domain.member.dto.ChangeMemberPasswordRequest;
import com.vong.manidues.domain.member.dto.MemberVerificationRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static com.vong.manidues.domain.auth.AuthenticationFixture.MEMBER_EMAIL;
import static com.vong.manidues.domain.auth.AuthenticationFixture.PASSWORD;

@SpringBootTest
@ActiveProfiles("test")
public class MemberServiceTest {

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

        ChangeMemberPasswordRequest request2 =
                ChangeMemberPasswordRequest.builder()
                        .email(email)
                        .currentPassword(changedPassword)
                        .changedPassword(password)
                        .changedPasswordCheck(password)
                        .build();
        Assertions.assertTrue(service.changePassword(request2));
    }
}
