package com.gosqo.flyinheron.controller;

import com.gosqo.flyinheron.dto.member.*;
import com.gosqo.flyinheron.service.MemberService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {

    private final MemberService service;
    private final AuthCookieManager cookieManager;

    @PostMapping("")
    public ResponseEntity<Object> register(
            @Valid @RequestBody MemberRegisterRequest request
            , HttpServletResponse response
    ) {
        Map<String, String> tokens = service.register(request);

        cookieManager.addRefreshTokenCookie(tokens.get("refreshToken"), response);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(MemberRegisterResponse.builder()
                        .status(HttpStatus.CREATED.value())
                        .message("회원가입에 성공했습니다.")
                        .accessToken(tokens.get("accessToken"))
                        .build()
                );
    }

    @PostMapping("/isUniqueEmail")
    public ResponseEntity<Object> isUniqueEmail(
            @Valid @RequestBody IsUniqueEmailRequest request
    ) {
        boolean isUnique = service.isUniqueEmail(request);
        HttpStatus statusCode = isUnique ? HttpStatus.OK : HttpStatus.CONFLICT;
        String message = isUnique ? "사용 가능한 이메일 주소입니다." : "이미 존재하는 이메일입니다.";

        return ResponseEntity
                .status(statusCode)
                .body(IsUniqueEmailResponse.builder()
                        .status(statusCode.value())
                        .message(message)
                        .build());
    }

    @PostMapping("/isUniqueNickname")
    public ResponseEntity<Object> isUniqueNickname(
            @Valid @RequestBody IsUniqueNicknameRequest request
    ) {
        boolean isUnique = service.isUniqueNickname(request);
        HttpStatus statusCode = isUnique ? HttpStatus.OK : HttpStatus.CONFLICT;
        String message = isUnique ? "사용 가능한 닉네임입니다." : "이미 존재하는 닉네임입니다.";

        return ResponseEntity
                .status(statusCode)
                .body(IsUniqueNicknameResponse.builder()
                        .status(statusCode.value())
                        .message(message)
                        .build());
    }
}
