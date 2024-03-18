package com.vong.manidues.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Controller
public class ViewController {

    @GetMapping("/")
    public String getHome() {
        log.info("request to \"/\" ... ");
        return "index";
    }

    @GetMapping("/test")
    public void test() { log.info("request to \"/test\" ... "); }

    @GetMapping("/board/{id}")
    public String getBoard(@PathVariable("id") Long id) {
        log.info("request to board id: {}", id);
        // TODO handling 404
        return "board/board";
    }
}
