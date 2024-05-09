package com.vong.manidues.token;

import com.vong.manidues.member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TokenUtility {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    JwtService service;

    public String issueAccessTokenOnTest(Long memberId) {
        return "Bearer " + service.generateAccessToken(memberRepository.findById(memberId).orElseThrow());
    }
}
