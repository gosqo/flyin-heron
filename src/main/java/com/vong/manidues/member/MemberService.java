package com.vong.manidues.member;

import com.vong.manidues.member.dto.MemberRegisterRequest;
import org.springframework.stereotype.Service;

@Service
public interface MemberService {

    public boolean register(MemberRegisterRequest request);

}
