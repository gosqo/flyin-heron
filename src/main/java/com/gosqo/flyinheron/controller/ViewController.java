package com.gosqo.flyinheron.controller;

import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ViewController {

    @GetMapping("/")
    public String getHome() {
        return "index";
    }

    @GetMapping("/login")
    public String viewLogin() {
        return "member/login";
    }

    @GetMapping("/signUp")
    public String viewSignUp() {
        return "member/signUp";
    }

    @GetMapping("/board/{id}")
    public String getBoard(@NotNull @PathVariable("id") Long id) {
        return "board/boardView";
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/board/{id}/modify")
    public String modifyBoard(@NotNull @PathVariable("id") Long id) {
        return "board/boardModify";
    }

    @GetMapping("/board")
    public String getBoardListView(@Nullable @RequestParam("page") Integer pageNumber) {
        return "board/boardList";
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/board/new")
    public String newBoard() {
        return "board/boardNew";
    }
}
