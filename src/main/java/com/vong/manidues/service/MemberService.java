package com.vong.manidues.service;

import com.vong.manidues.dto.member.*;
import org.springframework.stereotype.Service;

@Service
public interface MemberService {
    void register(MemberRegisterRequest request);

    IsUniqueEmailResponse isUniqueEmail(IsUniqueEmailRequest request);

    IsUniqueNicknameResponse isUniqueNickname(IsUniqueNicknameRequest request);

    boolean changePassword(ChangeMemberPasswordRequest request);

    boolean verifyOneself(MemberVerificationRequest request);
}
