package com.vong.manidues.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNoResourceFoundException(NoResourceFoundException ex) {
        return "error/404";
    }

    @ExceptionHandler(RuntimeException.class)
    public String handleInternalServerError(RuntimeException ex) {
        return "error/500";
    }
    //MethodArgumentTypeMismatchException
}
