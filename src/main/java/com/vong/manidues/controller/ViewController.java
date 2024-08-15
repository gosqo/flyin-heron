package com.vong.manidues.controller;

import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ViewController {

    @GetMapping("/")
    public String getHome() {
        return "index";
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

    @GetMapping(value = {"/boards", "/boards/{pageNumber}"})
    public String getBoardListView(@Nullable @PathVariable("pageNumber") Integer pageNumber) {
        return "board/boardList";
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/board/new")
    public String newBoard() {
        return "board/boardNew";
    }
}
