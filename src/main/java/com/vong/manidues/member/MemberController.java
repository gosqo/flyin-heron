package com.vong.manidues.member;

import com.vong.manidues.member.dto.IsPresentEmailRequest;
import com.vong.manidues.member.dto.IsPresentNicknameRequest;
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
        return service.register(request)
                ? ResponseEntity.ok("회원가입에 성공했습니다.")
                : ResponseEntity.status(400)
                .body(
                        JsonResponseBody.builder()
                                .message("중복 확인 메세지를 확인해주시기 바랍니다.")
                                .build()
                );
    }

    @PostMapping("/isPresentEmail")
    public ResponseEntity<Object> isPresentEmail(
            @Valid @RequestBody IsPresentEmailRequest request
    ) {
        return repository.findByEmail(request.getEmail()).isPresent()
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
    public ResponseEntity<Object> isPresentNickname(
            @Valid @RequestBody IsPresentNicknameRequest request
    ) {
        return repository.findByNickname(request.getNickname()).isPresent()
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
