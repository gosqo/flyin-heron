package com.vong.manidues.exception;

import com.vong.manidues.utility.JsonResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.NoSuchElementException;

@ControllerAdvice
@Slf4j
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

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Object> handleNoSuchElementException(
            NoSuchElementException ex
    ) {
        log.info("getMessage(): {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(JsonResponseBody.builder()
                        .message(ex.getMessage())
                        .build()
                );
    }
}
