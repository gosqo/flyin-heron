package com.vong.manidues.exception;

import com.vong.manidues.utility.JsonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
@Slf4j
public class GlobalRestExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex
    ) {
        String validExceptionMessage = ex.getFieldErrors().get(0).getDefaultMessage();

        log.info("getFieldsErrors(): {}", validExceptionMessage);

        return ResponseEntity
                .status(ex.getStatusCode().value())
                .body(JsonResponse.builder()
                        .message(validExceptionMessage)
                        .build()
                );
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Object> handleNoSuchElementException(
            NoSuchElementException ex
    ) {
        log.info("cause.message(): {}", ex.getCause().getMessage());
        log.info("cause.class(): {}", ex.getCause().getClass());
        log.info("cause.localizedMessage(): {}", ex.getCause().getLocalizedMessage());
        log.info("getMessage(): {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(JsonResponse.builder()
                        .message(ex.getMessage())
                        .build()
                );
    }
}
