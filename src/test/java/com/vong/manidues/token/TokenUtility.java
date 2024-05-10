package com.vong.manidues.token;

import com.vong.manidues.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenUtility {

    private final MemberRepository memberRepository;
    private final JwtService service;

    public String issueAccessTokenOnTest(Long memberId) {
        return "Bearer " + service.generateAccessToken(memberRepository.findById(memberId).orElseThrow());
    }
}
