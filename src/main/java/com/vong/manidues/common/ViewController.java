package com.vong.manidues.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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
}
