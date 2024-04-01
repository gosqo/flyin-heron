package com.vong.manidues.member.controller;

import com.vong.manidues.member.MemberRepository;
import com.vong.manidues.member.MemberService;
import com.vong.manidues.member.dto.IsPresentRequest;
import com.vong.manidues.member.dto.MemberRegisterRequest;
import com.vong.manidues.utility.JsonResponseBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
@Slf4j
public class MemberController {

    private final MemberService service;
    private final MemberRepository repository;

    @PostMapping("/")
    public ResponseEntity<Object> register(
            @Valid @RequestBody MemberRegisterRequest request
    ) {
        log.info("request POST to \"/api/v1/member/\"");

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

    @PostMapping("/isPresentEmail")
    public ResponseEntity<Object> isPresentEmail(@Valid @RequestBody IsPresentRequest request) {
        boolean doesEmailPresent = repository.findByEmail(request.getValueToCheck()).isPresent();

        log.info("""
                        request to /isPresentEmail with email: {}
                        does email present on DB?: {}
                        """,
                request.getValueToCheck(),
                doesEmailPresent
                );

        return doesEmailPresent
                ? ResponseEntity.status(409).body(
                        JsonResponseBody.builder()
                                .message("이미 가입한 이메일 주소입니다.")
                                .build()
                )
                : ResponseEntity.status(200).body(
                        JsonResponseBody.builder()
                                .message("사용 가능한 이메일 주소입니다.")
                                .build()
                );
    }

    @PostMapping("/isPresentNickname")
    public ResponseEntity<Object> isPresentNickname(@Valid @RequestBody IsPresentRequest request) {
        boolean doesNicknamePresent = repository.findByNickname(request.getValueToCheck()).isPresent();

        log.info("""
                        request to /isPresentNickname with email: {}
                        does nickname present on DB?: {}
                        """,
                request.getValueToCheck(),
                doesNicknamePresent
        );

        return doesNicknamePresent
                ? ResponseEntity.status(409).body(
                        JsonResponseBody.builder()
                                .message("이미 존재하는 닉네임입니다.")
                                .build()
                )
                : ResponseEntity.status(200).body(
                        JsonResponseBody.builder()
                                .message("사용 가능한 닉네임입니다.")
                                .build()
                );
    }
}
