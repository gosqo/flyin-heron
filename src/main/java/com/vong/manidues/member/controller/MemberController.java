package com.vong.manidues.member.controller;

import com.vong.manidues.member.MemberService;
import com.vong.manidues.member.dto.MemberDTO;
import com.vong.manidues.member.dto.MemberLoginRequest;
import com.vong.manidues.member.dto.MemberRegisterRequest;
import com.vong.manidues.member.dto.MemberRegisterResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @PostMapping(value = "login/process")
    public ResponseEntity<Object> login(
            @RequestBody MemberLoginRequest request) throws IOException {

        log.info("request to /login/process ... ");

        String token = memberService.login(request.getEmail(), request.getPassword());
        log.info("token: {}", token);

        return ResponseEntity.status(HttpStatus.OK).body(token);
    }

    @PostMapping("member")
    public ResponseEntity<MemberRegisterResponse> register(@RequestBody MemberRegisterRequest request) {
        log.info("request 'POST' /member");
        MemberDTO memberDTO = memberService.register(request);
        return new ResponseEntity<>(new MemberRegisterResponse(memberDTO.getNickname()), HttpStatus.OK);
    }
}
