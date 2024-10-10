package com.gosqo.flyinheron.global.exception;

import com.gosqo.flyinheron.global.exception.custom.DebugNeededException;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    private static final String PROJECT_ROOT_PACKAGE = "com.gosqo.flyinheron";

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

    private String exceptionNameAndMessage(Exception ex) {
        return String.format("%s: %s", ex.getClass().getName(), ex.getMessage());
    }

    private String whichThrow(Exception ex) {
        return String.format("thrown in: %s\n\tclosest Project files is: %s"
                , getFirstFileFromStackTrace(ex)
                , getFirstProjectFileFromStackTrace(ex.getStackTrace())
        );
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

    @ExceptionHandler(AccessDeniedException.class)
    public Object handleAccessDeniedException(
            AccessDeniedException ex
            , HttpServletRequest request
            , HttpServletResponse response
    ) {
        log.info(exceptionNameAndMessage(ex));
        log.warn("", ex);

        if (request.getMethod().equalsIgnoreCase(HttpMethod.GET.name())) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return "error/403";
        }

        String userMessage = "로그인 후 이용해주시기 바랍니다.";

        return buildResponseEntity(HttpStatus.FORBIDDEN, userMessage);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public Object handleNoResourceFoundException(
            NoResourceFoundException ex
            , HttpServletResponse response
    ) {
        log.info(exceptionNameAndMessage(ex));
        log.warn(whichThrow(ex));

        if (ex.getHttpMethod().matches(HttpMethod.GET.name())) {
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
        log.info(exceptionNameAndMessage(ex));
        log.warn(whichThrow(ex));
        String userMessage = "이메일 혹은 패스워드를 확인 해주세요.";

        return buildResponseEntity(HttpStatus.BAD_REQUEST, userMessage);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex
    ) {
        log.info(exceptionNameAndMessage(ex));
        log.warn(whichThrow(ex));

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

        log.warn("", ex);

        return buildResponseEntity(HttpStatus.BAD_REQUEST, userMessage);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(
            AuthenticationException ex
    ) {
        log.info(exceptionNameAndMessage(ex));
        log.warn("", ex);
        String userMessage = "인증에 실패했습니다.";

        return buildResponseEntity(HttpStatus.BAD_REQUEST, userMessage);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElementException(
            NoSuchElementException ex
    ) {
        log.info(exceptionNameAndMessage(ex));
        log.warn(whichThrow(ex));
        String userMessage = "존재하지 않는 자원에 대한 요청입니다.";

        return buildResponseEntity(HttpStatus.BAD_REQUEST, userMessage);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex
    ) {
        log.info(exceptionNameAndMessage(ex));
        log.warn(whichThrow(ex));
        String userMessage = ex.getFieldErrors().get(0).getDefaultMessage();

        return buildResponseEntity(HttpStatus.BAD_REQUEST, userMessage);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex
    ) {
        log.info(exceptionNameAndMessage(ex));
        log.warn(whichThrow(ex));
        String userMessage = "올바른 요청이 아닙니다.";

        return buildResponseEntity(HttpStatus.BAD_REQUEST, userMessage);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> handleNullPointerException(
            NullPointerException ex
    ) {
        log.info(exceptionNameAndMessage(ex));
        log.warn("", ex);
        String userMessage = "서버 오류가 발생했습니다. 문제가 지속될 시 운영진에 연락 부탁드립니다.";

        return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, userMessage);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handleExpiredJwtException(
            ExpiredJwtException ex
    ) {
        log.info(exceptionNameAndMessage(ex));
        String userMessage = "토큰이 만료되어 서버에 도달했습니다.";

        return buildResponseEntity(HttpStatus.UNAUTHORIZED, userMessage);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Object handleHttpRequestMethodNotSupportedException(
            HttpServletRequest request
            , HttpServletResponse response
            , HttpRequestMethodNotSupportedException ex
    ) {
        log.info(exceptionNameAndMessage(ex));

        if (request.getMethod().matches(HttpMethod.GET.name())) {
            response.setStatus(404);
            return "error/404";
        }

        String userMessage = "API 사용에 합의되지 않은 요청입니다.";
        String supportedMethod = Objects.requireNonNull(ex.getSupportedHttpMethods())
                .stream()
                .map(HttpMethod::toString)
                .collect(Collectors.joining(", "));

        response.setHeader(HttpHeaders.ALLOW, supportedMethod);

        return buildResponseEntity(HttpStatus.METHOD_NOT_ALLOWED, userMessage);
    }

    @ExceptionHandler(DebugNeededException.class)
    public ResponseEntity<ErrorResponse> handleDebugNeededException(
            DebugNeededException ex
    ) {
        log.info(exceptionNameAndMessage(ex));
        log.warn("", ex);
        String userMessage = "서버 오류가 발생했습니다. 문제가 지속될 시 운영진에 연락 부탁드립니다.";

        return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, userMessage);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException ex
    ) {
        log.info(exceptionNameAndMessage(ex));
        log.warn("", ex);
        String userMessage = "서버 오류가 발생했습니다. 문제가 지속될 시 운영진에 연락 부탁드립니다.";

        return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, userMessage);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception ex
    ) {
        log.info(exceptionNameAndMessage(ex));
        log.warn("", ex);
        String userMessage = "서버 오류가 발생했습니다. 문제가 지속될 시 운영진에 연락 부탁드립니다.";

        return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, userMessage);
    }
}
