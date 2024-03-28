package com.vong.manidues.member.controller;

import com.vong.manidues.member.MemberService;
import com.vong.manidues.member.dto.MemberRegisterRequest;
import com.vong.manidues.utility.JsonResponseBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
            @Valid @RequestBody MemberRegisterRequest request
    ) {
        log.info("request 'POST' /member " + request);
        return service.register(request)
                ? ResponseEntity.ok("회원가입에 성공했습니다.")
                : ResponseEntity.status(400)
                .body(
                        JsonResponseBody.builder()
                                .statusCode(400)
                                .message("각 입력란의 양식에 맞춰 입력해주시기 바랍니다.")
                                .build()
                );
    }
}
