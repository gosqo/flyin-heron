package com.vong.manidues.member.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class MemberViewController {

    @GetMapping("/login")
    public String viewLogin() {
        log.info("request to \"/login\" ... ");
        return "member/login";
    }

    @GetMapping("/signUp")
    public String viewSigunUp() {
        log.info("request to \"signUp\" ... ");
        return "member/signUp";
    }
}
