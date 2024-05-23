package com.vong.manidues.member;

import com.vong.manidues.member.dto.ChangeMemberPasswordRequest;
import com.vong.manidues.member.dto.MemberRegisterRequest;
import com.vong.manidues.member.dto.MemberVerificationRequest;
import org.springframework.stereotype.Service;

@Service
public interface MemberService {
    public void register(MemberRegisterRequest request);
    public boolean changePassword(ChangeMemberPasswordRequest request);
    public boolean verifyOneself(MemberVerificationRequest request);
}
