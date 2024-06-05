package com.vong.manidues.member;

import com.vong.manidues.member.dto.*;
import org.springframework.stereotype.Service;

@Service
public interface MemberService {
    void register(MemberRegisterRequest request);
    IsUniqueEmailResponse isUniqueEmail(IsUniqueEmailRequest request);
    IsUniqueNicknameResponse isUniqueNickname(IsUniqueNicknameRequest request);
    boolean changePassword(ChangeMemberPasswordRequest request);
    boolean verifyOneself(MemberVerificationRequest request);
}
