package com.vong.manidues.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.NoSuchElementException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private ResponseEntity<ErrorResponse> buildResponseEntity(
            HttpStatus status, String message
    ) {
        return ResponseEntity
                .status(status)
                .body(ErrorResponse.builder()
                        .status(status.value())
                        .message(message)
                        .build());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElementException(
            NoSuchElementException ex
    ) {
        log.info("NoSuchElementException: {}", ex.getMessage());
        String userMessage = "존재하지 않는 자원에 대한 접근입니다.";

        return buildResponseEntity(HttpStatus.NOT_FOUND, userMessage);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex
    ) {
        log.warn("MethodArgumentTypeMismatchException: {}", ex.getMessage());
        String userMessage = "올바른 요청이 아닙니다.";

        return buildResponseEntity(HttpStatus.BAD_REQUEST, userMessage);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> handleNullPointerException(
            NullPointerException ex
    ) {
        log.error("NullPointerException: ", ex);
        String userMessage = "내부 오류가 발생했습니다. 문제가 지속될 시 운영진에 연락 부탁드립니다.";

        return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, userMessage);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException ex
    ) {
        log.error("RuntimeException: ", ex);
        String userMessage = "내부 오류가 발생했습니다. 문제가 지속될 시 운영진에 연락 부탁드립니다.";

        return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, userMessage);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception ex
    ) {
        log.error("ErrorResponse: ", ex);
        String userMessage = "내부 오류가 발생했습니다. 문제가 지속될 시 운영진에 연락 부탁드립니다.";

        return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, userMessage);
    }
}
