package com.vong.manidues.member;

import com.vong.manidues.member.dto.MemberDTO;
import com.vong.manidues.member.dto.MemberLoginRequest;
import com.vong.manidues.member.dto.MemberRegisterRequest;
import com.vong.manidues.member.dto.MemberRegisterResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("member")
    public ResponseEntity<MemberRegisterResponse> register(@RequestBody MemberRegisterRequest request) {
        MemberDTO memberDTO = memberService.register(request);
        return new ResponseEntity<>(new MemberRegisterResponse(memberDTO.getNickname()), HttpStatus.OK);
    }
}
