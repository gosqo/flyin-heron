package com.vong.manidues.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class CommonController {

    @GetMapping("/")
    public String getHome() {
        log.info("request to '/' ... ");

        return "index";
    }

    @GetMapping("/test")
    public void getTest() {

        log.info("live?");
        log.info("message");
//        int i = 0;
        log.info("request to home {}", "hello?");
        log.info("does livereload working?");

    }

}
