package com.vong.manidues.member;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class MemberViewController {

    @GetMapping("/login")
    public String viewLogin() {
        return "member/login";
    }

    @GetMapping("/signUp")
    public String viewSigunUp() {
        return "member/signUp";
    }
}
