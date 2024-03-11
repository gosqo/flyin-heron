package com.vong.manidues.member.controller;

import com.vong.manidues.member.MemberService;
import com.vong.manidues.member.dto.MemberRegisterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService service;

    @PostMapping("/member")
    public ResponseEntity<Object> register(
            @RequestBody MemberRegisterRequest request
    ) {
        log.info("request 'POST' /member " + request);
        return service.register(request) ? ResponseEntity.ok("회원가입에 성공했습니다.") : new ResponseEntity<>("기입한 내용에 문제가 있습니다.\n회원 가입 가이드와 양식에 따라 정보를 기입해주세요.", HttpStatus.CONFLICT);
    }
}
