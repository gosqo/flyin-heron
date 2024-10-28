package com.gosqo.flyinheron.global.exception;

import com.gosqo.flyinheron.global.exception.custom.DebugNeededException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
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

import java.util.*;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    private static final List<Set<Class<? extends Exception>>> EXPECTED_EXCEPTIONS = new ArrayList<>();
    private static final Set<Class<? extends Exception>> BAD_REQUEST_EXCEPTIONS = Set.of(
            MethodArgumentNotValidException.class
            , MethodArgumentTypeMismatchException.class
            , BadCredentialsException.class
            , DataIntegrityViolationException.class
            , IllegalArgumentException.class
    );
    private static final Set<Class<? extends Exception>> UNAUTHORIZED_EXCEPTIONS = Set.of(
            ExpiredJwtException.class
    );

    private static final Set<Class<? extends Exception>> FORBIDDEN_EXCEPTIONS = Set.of(
            javax.naming.AuthenticationException.class
            , AccessDeniedException.class
            , JwtException.class
    );

    private static final Set<Class<? extends Exception>> NOT_FOUND_EXCEPTIONS = Set.of(
            NoResourceFoundException.class
            , NoSuchElementException.class
            , HttpRequestMethodNotSupportedException.class
    );
    private static final Set<Class<? extends Exception>> INTERNAL_SERVER_EXCEPTIONS = Set.of(
            NullPointerException.class
            , DebugNeededException.class
            , RuntimeException.class
            , Exception.class
    );
    private static final Set<Class<? extends Exception>> STACK_TRACE_NEEDED = Set.of(
            NullPointerException.class
            , DebugNeededException.class
            , RuntimeException.class
            , Exception.class
    );

    static {
        EXPECTED_EXCEPTIONS.add(BAD_REQUEST_EXCEPTIONS);
        EXPECTED_EXCEPTIONS.add(UNAUTHORIZED_EXCEPTIONS);
        EXPECTED_EXCEPTIONS.add(FORBIDDEN_EXCEPTIONS);
        EXPECTED_EXCEPTIONS.add(NOT_FOUND_EXCEPTIONS);
    }

    @ExceptionHandler(Exception.class)
    public Object handleGetRequestExceptions(Exception e, HttpServletRequest request, HttpServletResponse response) {

        if (EXPECTED_EXCEPTIONS.stream().noneMatch(
                setOfExceptions -> setOfExceptions.contains(e.getClass()))
        ) {
            log.warn("", e);
        }

        if (request.getMethod().equalsIgnoreCase(HttpMethod.GET.name())) {

            if (BAD_REQUEST_EXCEPTIONS.contains(e.getClass())) {
                response.setStatus(400);
                return "error/400";
            }

            if (UNAUTHORIZED_EXCEPTIONS.contains(e.getClass())) {
                response.setStatus(401);
                return "error/401";
            }

            if (FORBIDDEN_EXCEPTIONS.contains(e.getClass())) {
                response.setStatus(403);
                return "error/403";
            }

            if (NOT_FOUND_EXCEPTIONS.contains(e.getClass())) {
                response.setStatus(404);
                return "error/404";
            }

            if (INTERNAL_SERVER_EXCEPTIONS.contains(e.getClass())) {
                response.setStatus(500);
                return "error/500";
            }
        }

        return delegateHandling(response, e);
    }

    public <E extends Exception> ResponseEntity<ErrorResponse> delegateHandling(
            HttpServletResponse response
            , E e
    ) {
        log.warn(exceptionNameAndMessage(e));

        // log stack trace if needed.
        if (EXPECTED_EXCEPTIONS.stream().noneMatch(
                setOfExceptions -> setOfExceptions.contains(e.getClass()))
        ) {
            log.warn("", e);
        }

        // exception instance check.
        if (e instanceof ExpiredJwtException) {
            return buildResponseEntity(
                    HttpStatus.UNAUTHORIZED
                    , "회원이라면 잠시 후 다시 시도해주세요."
            );
        }

        if (e instanceof BadCredentialsException) {
            return buildResponseEntity(
                    HttpStatus.BAD_REQUEST
                    , "이메일 혹은 패스워드를 확인 해주세요."
            );
        }

        if (e instanceof AccessDeniedException) {
            return buildResponseEntity(
                    HttpStatus.FORBIDDEN
                    , "로그인 후 이용해주시기 바랍니다."
            );
        }

        if (e instanceof AuthenticationException) {
            return buildResponseEntity(
                    HttpStatus.FORBIDDEN
                    , "인증 정보에 문제가 있습니다."
            );
        }

        if (e instanceof MethodArgumentNotValidException) {
            return buildResponseEntity(
                    HttpStatus.BAD_REQUEST
                    , ((MethodArgumentNotValidException) e).getFieldErrors().get(0).getDefaultMessage()
            );
        }

        if (e instanceof DataIntegrityViolationException) {
            return buildResponseEntity(
                    HttpStatus.CONFLICT
                    , "자원의 입력 및 수정이 중복을 발생시킵니다. 이 중복은 허용되지 않습니다."
            );
        }

        if (e instanceof NoSuchElementException
                || e instanceof NoResourceFoundException
        ) {
            return buildResponseEntity(
                    HttpStatus.NOT_FOUND
                    , "존재하지 않는 자원에 대한 요청입니다."
            );
        }

        if (e instanceof IllegalArgumentException
                || e instanceof MethodArgumentTypeMismatchException
        ) {
            return buildResponseEntity(
                    HttpStatus.BAD_REQUEST
                    , "올바르지 않은 요청입니다."
            );
        }

        if (e instanceof HttpRequestMethodNotSupportedException) {
            String supportedMethod = Objects.requireNonNull(
                            ((HttpRequestMethodNotSupportedException) e).getSupportedHttpMethods())
                    .stream()
                    .map(HttpMethod::toString)
                    .collect(Collectors.joining(", "));

            response.setHeader(HttpHeaders.ALLOW, supportedMethod);

            return buildResponseEntity(
                    HttpStatus.METHOD_NOT_ALLOWED
                    , "API 사용에 합의되지 않은 요청입니다."
            );
        }

        return buildResponseEntity(
                HttpStatus.INTERNAL_SERVER_ERROR
                , "서버 오류가 발생했습니다. 문제가 지속될 시 운영진에 연락 부탁드립니다."
        );
    }

    private String exceptionNameAndMessage(Exception ex) {
        return String.format("%s: %s", ex.getClass().getName(), ex.getMessage());
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
