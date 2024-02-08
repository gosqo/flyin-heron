package com.vong.manidues.member;

import com.vong.manidues.member.dto.MemberLoginRequest;
import com.vong.manidues.member.dto.MemberLoginResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LoginController {

    private final MemberService memberService;

    @PostMapping(value = "login/process")
//    public ResponseEntity<MemberLoginResponse> login(
    public ResponseEntity<Object> login(
            @RequestBody MemberLoginRequest request/*,
            HttpServletResponse response*/) throws IOException {


        String token = memberService.login(request.getEmail(), request.getPassword());
        log.info("token: {}", token);

//        response.sendRedirect("/");

//        return new ResponseEntity<>(new MemberLoginResponse(token), HttpStatus.OK);

        return ResponseEntity.status(HttpStatus.OK).body(token);
    }

}
