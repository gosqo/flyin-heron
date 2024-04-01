package com.vong.manidues.common.exception;

import com.vong.manidues.utility.JsonResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalRestExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {

        String validExceptionMessage = ex.getFieldErrors().get(0).getDefaultMessage();

        log.info("\ngetFieldsErrors(): {}\n", validExceptionMessage);

        return ResponseEntity
                .status(ex.getStatusCode().value())
                .body(JsonResponseBody.builder()
                        .statusCode(ex.getStatusCode().value())
                        .message(validExceptionMessage)
                        .build()
                );
    }

    // NoSuchElementException DB 조회 관련 에러 처리.

}
