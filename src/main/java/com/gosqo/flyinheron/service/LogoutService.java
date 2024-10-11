package com.gosqo.flyinheron.service;

import com.gosqo.flyinheron.domain.Token;
import com.gosqo.flyinheron.dto.ResponseBodyWriter;
import com.gosqo.flyinheron.global.utility.AuthHeaderUtility;
import com.gosqo.flyinheron.global.utility.CookieUtility;
import com.gosqo.flyinheron.repository.TokenRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

import static com.gosqo.flyinheron.controller.AuthenticationController.REFRESH_TOKEN_COOKIE_NAME;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogoutService implements LogoutHandler {

    private final TokenRepository tokenRepository;
    private final ResponseBodyWriter responseBodyWriter;

    /**
     * <p>정상정 요청이라면 사용자는 헤더에 리프레시 토큰을 담아 요청.
     * <p>데이터베이스에서 refreshToken 에 일치하는 모든 토큰을 찾습니다. 조회 결과를 {@code List<Token>} 의 형식으로 받습니다.
     *
     * <pre>
     * {@code List<token>.isEmpty} 가 참이라면, (조회되는 토큰이 없는 상황)
     *   log.info("user tried refresh token that does not exist on database.");
     *   사용자에게 상태 코드 400과 함께 '잘못된 접근'을 응답합니다.
     * </pre>
     * <pre>
     * {@code List<Token>} 의 크기가 1 이상 이라면,
     *   토큰이 2 이상인지 확인, (리스트의 사이즈 부터 확인합니다.)
     *       토큰 중복 저장의 warn 로그를 남깁니다.
     *   데이터베이스에서 해당 토큰 모두를 삭제합니다.
     *   사용자에게는 '정상적 처리'를 응답합니다. </pre>
     */
    @Override
//    @PreAuthorize("hasRole('ROLE_USER')") // 활성 시, SecurityContext 비어있으면 500 에러
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        final String refreshToken = AuthHeaderUtility.extractRefreshToken(request);
        final List<Token> storedTokens = tokenRepository.findAllByToken(refreshToken).stream().toList();

        if (storedTokens.isEmpty()) { // refreshToken entity 가 존재하지 않는다면,
            log.warn("user tried refresh token that does not exist on database.");
            responseWith400(response);

            return;
        }

        int deletedTokenCount = tokenRepository.deleteByToken(refreshToken);
        log.info("Deleted token count is: {}", deletedTokenCount);

        Cookie deleteRefreshToken = CookieUtility.findCookie(REFRESH_TOKEN_COOKIE_NAME, request.getCookies());
        deleteRefreshToken.setMaxAge(0);
        deleteRefreshToken.setPath("/");
        response.addCookie(deleteRefreshToken);

        responseWith200(request, response);
    }

    private void responseWith400(HttpServletResponse response) {
        try {
            responseBodyWriter.setResponseWithBody(
                    response
                    , 400
                    , "올바른 요청이 아닙니다.");
        } catch (IOException e) {
            log.info(e.getMessage());
        }
    }

    private void responseWith200(HttpServletRequest request, HttpServletResponse response) {
        try {
            responseBodyWriter.setResponseWithBody(
                    response
                    , 200
                    , "로그아웃 했습니다.");
        } catch (IOException e) {
            log.info(e.getMessage());
        }
    }
}
