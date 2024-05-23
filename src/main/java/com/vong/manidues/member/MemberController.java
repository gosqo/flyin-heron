package com.vong.manidues.member;

import com.vong.manidues.member.dto.IsPresentEmailRequest;
import com.vong.manidues.member.dto.IsPresentNicknameRequest;
import com.vong.manidues.member.dto.MemberRegisterRequest;
import com.vong.manidues.utility.JsonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

    @PostMapping("")
    public ResponseEntity<Object> register(
            @Valid @RequestBody MemberRegisterRequest request
    ) {
        service.register(request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("회원가입에 성공했습니다.");
    }

    @PostMapping("/isPresentEmail")
    public ResponseEntity<Object> isPresentEmail(
            @Valid @RequestBody IsPresentEmailRequest request
    ) {
        return repository.findByEmail(request.getEmail()).isPresent()
                ? ResponseEntity.status(409).body(
                JsonResponse.builder()
                        .message("이미 가입한 이메일 주소입니다.")
                        .build()
        )
                : ResponseEntity.status(200).body(
                JsonResponse.builder()
                        .message("사용 가능한 이메일 주소입니다.")
                        .build()
        );
    }

    @PostMapping("/isPresentNickname")
    public ResponseEntity<Object> isPresentNickname(
            @Valid @RequestBody IsPresentNicknameRequest request
    ) {
        return repository.findByNickname(request.getNickname()).isPresent()
                ? ResponseEntity.status(409).body(
                JsonResponse.builder()
                        .message("이미 존재하는 닉네임입니다.")
                        .build()
        )
                : ResponseEntity.status(200).body(
                JsonResponse.builder()
                        .message("사용 가능한 닉네임입니다.")
                        .build()
        );
    }
}
