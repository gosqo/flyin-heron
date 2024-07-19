package com.vong.manidues.global.exception;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/exception")
public class ExceptionTestController {
    @GetMapping("")
    public void throwNullPointerException() {
        throw new NullPointerException("hello, this NPE thrown by exception test controller.");
    }
}
