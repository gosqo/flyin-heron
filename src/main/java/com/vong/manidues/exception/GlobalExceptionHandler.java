package com.vong.manidues.exception;

import com.vong.manidues.exception.custom.DebugNeededException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Arrays;
import java.util.NoSuchElementException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    private static final String PROJECT_ROOT_PACKAGE = "com.vong.manidues";

    @ExceptionHandler(AccessDeniedException.class)
    public <T> Object handleAccessDeniedException(
            AccessDeniedException ex
            , HttpServletRequest request
            , HttpServletResponse response
    ) {
        logException(ex);
        logErrorStackTrace(ex);

        if (request.getMethod().equalsIgnoreCase(HttpMethod.GET.name())) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return "error/403";
        }

        String userMessage = "올바른 접근이 아닙니다.";

        return buildResponseEntity(HttpStatus.FORBIDDEN, userMessage);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public <T> Object handleNoResourceFoundException(
            NoResourceFoundException ex
            , HttpServletRequest request
            , HttpServletResponse response
    ) {
        logException(ex);
        logWhereThrows(ex);

        if (request.getMethod().equalsIgnoreCase(HttpMethod.GET.name())) {
            response.setStatus(404);
            return "error/404";
        }

        String userMessage = "존재하지 않는 자원에 대한 요청입니다.";

        return buildResponseEntity(HttpStatus.NOT_FOUND, userMessage);


    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(
            BadCredentialsException ex
    ) {
        logException(ex);
        logWhereThrows(ex);
        String userMessage = "이메일 혹은 패스워드를 확인 해주세요.";

        return buildResponseEntity(HttpStatus.BAD_REQUEST, userMessage);
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex
    ) {
        logException(ex);
        logWhereThrows(ex);

        String userMessage;

        switch (whichMethodThrow(ex)) {
            case "isUniqueEmail" -> {
                userMessage = "이미 가입한 이메일입니다.";
                return buildResponseEntity(HttpStatus.CONFLICT, userMessage);
            }
            case "isUniqueNickname" -> {
                userMessage = "사용 중인 닉네임입니다.";
                return buildResponseEntity(HttpStatus.CONFLICT, userMessage);
            }
            default -> userMessage = "자원의 입력 및 수정이 지정된 형식에 맞지 않거나 중복을 발생시킵니다.";
        }

        logErrorStackTrace(ex);

        return buildResponseEntity(HttpStatus.BAD_REQUEST, userMessage);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(
            AuthenticationException ex
    ) {
        logException(ex);
        logErrorStackTrace(ex);
        String userMessage = "인증에 실패했습니다.";

        return buildResponseEntity(HttpStatus.BAD_REQUEST, userMessage);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElementException(
            NoSuchElementException ex
    ) {
        logException(ex);
        logWhereThrows(ex);
        String userMessage = "존재하지 않는 자원에 대한 요청입니다.";

        return buildResponseEntity(HttpStatus.BAD_REQUEST, userMessage);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex
    ) {
        logException(ex);
        logWhereThrows(ex);
        String userMessage = ex.getFieldErrors().get(0).getDefaultMessage();

        return buildResponseEntity(HttpStatus.BAD_REQUEST, userMessage);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex
    ) {
        logException(ex);
        logWhereThrows(ex);
        String userMessage = "올바른 요청이 아닙니다.";

        return buildResponseEntity(HttpStatus.BAD_REQUEST, userMessage);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> handleNullPointerException(
            NullPointerException ex
    ) {
        logException(ex);
        logErrorStackTrace(ex);
        String userMessage = "서버 오류가 발생했습니다. 문제가 지속될 시 운영진에 연락 부탁드립니다.";

        return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, userMessage);
    }

    @ExceptionHandler(DebugNeededException.class)
    public ResponseEntity<ErrorResponse> handleDebugNeededException(
            DebugNeededException ex
    ) {
        logException(ex);
        logErrorStackTrace(ex);
        String userMessage = "서버 오류가 발생했습니다. 문제가 지속될 시 운영진에 연락 부탁드립니다.";

        return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, userMessage);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException ex
    ) {
        logException(ex);
        logErrorStackTrace(ex);
        String userMessage = "서버 오류가 발생했습니다. 문제가 지속될 시 운영진에 연락 부탁드립니다.";

        return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, userMessage);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception ex
    ) {
        logException(ex);
        logErrorStackTrace(ex);
        String userMessage = "서버 오류가 발생했습니다. 문제가 지속될 시 운영진에 연락 부탁드립니다.";

        return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, userMessage);
    }

    private void logException(Exception ex) {
        log.info("{}: {}", ex.getClass().getName(), ex.getMessage());
    }

    private void logWhereThrows(Exception ex) {
        log.warn("thrown in: {}\nclosest Project files is: {}"
                , getFirstFileFromStackTrace(ex)
                , getFirstProjectFileFromStackTrace(ex.getStackTrace())
        );
    }

    private void logErrorStackTrace(Exception ex) {
        log.error("", ex);
    }

    private static StackTraceElement[] filterProjectStackTrace(StackTraceElement[] stackTrace) {
        return Arrays.stream(stackTrace)
                .filter(element -> element.getClassName().startsWith(PROJECT_ROOT_PACKAGE))
                .toArray(StackTraceElement[]::new);
    }

    private static StackTraceElement getFirstProjectFileFromStackTrace(StackTraceElement[] stackTrace) {
        return filterProjectStackTrace(stackTrace)[0];
    }

    private static StackTraceElement getFirstFileFromStackTrace(Exception ex) {
        return ex.getStackTrace()[0];
    }

    private static String whichFileThrow(Exception ex) {
        return getFirstFileFromStackTrace(ex).getFileName();
    }

    private static String whichMethodThrow(Exception ex) {
        return getFirstFileFromStackTrace(ex).getMethodName();
    }

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
}
