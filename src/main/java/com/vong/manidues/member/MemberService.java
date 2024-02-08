package com.vong.manidues.member;

import com.vong.manidues.member.dto.MemberDTO;
import com.vong.manidues.member.dto.MemberRegisterRequest;
import org.springframework.stereotype.Service;

@Service
public interface MemberService {

    public MemberDTO register(MemberRegisterRequest request);

    public String login(String email, String password);


}
