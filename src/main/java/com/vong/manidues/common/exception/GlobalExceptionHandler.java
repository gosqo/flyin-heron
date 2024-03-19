package com.vong.manidues.common.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNoResourceFoundException(NoResourceFoundException ex) {
        return "/error/404";
    }

    @ExceptionHandler(Exception.class)
    public String handleInternalServerError(Exception ex) {
        return "/error/500";
    }

}
