package com.vong.manidues.config;

import com.vong.manidues.token.Token;
import com.vong.manidues.token.TokenRepository;
import com.vong.manidues.utility.HttpResponseWithBody;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogoutService implements LogoutHandler {

    private final TokenRepository tokenRepository;
    private final HttpResponseWithBody responseWithBody;

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        final String authHeader = request.getHeader("Authorization");
        final String refreshToken;

        // 클라이언트는 요청 헤더에 refresh_token 을 담아서 요청.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        refreshToken = authHeader.substring(7);
        /*
        * 대안 로직 (아래 주석으로 부터 올라옴)
        * 데이터베이스에서 refreshToken 에 일치하는 모든 토큰을 찾습니다. 조회 결과를 List<Token> 의 형식으로 받습니다.
        *
        * List<token>.isEmpty 가 참이라면, (조회되는 토큰이 없는 상황)
        *   log.info("user tried refresh token that does not exist on database.");
        *   사용자에게 상태 코드 400과 함께 '잘못된 접근'을 응답합니다.
        *
        * List<Token> 의 크기가 1 이상 이라면,
        *   토큰이 2 이상이라면, (리스트의 사이즈 부터 확인합니다.)
        *       warn 로그를 남깁니다. (refreshToken 의 저장 로직에 중복 저장 코드를 찾을 수 있도록)
        *   데이터베이스에서 해당 토큰 모두를 삭제합니다.
        *   사용자에게는 '정상적 처리'를 응답합니다.
        *
        * 위 대안으로 로직을 구성하면, IncorrectResultSizeDataAccessException 예외를 잡는 코드를 제거할 수 있습니다.
        * */
        List<Token> storedTokens = tokenRepository.findAllByToken(refreshToken).stream().toList();
        if (storedTokens.isEmpty()) {

            log.info("user tried refresh token that does not exist on database.");

            try {
                responseWithBody.jsonResponse(response, 400, "not exist on database");
            } catch (IOException e) {
                log.info(e.getMessage());
            }
            // refreshToken entity 가 존재한다면
        } else {

            if (storedTokens.size() > 1) {
                log.warn(" === Duplicated refresh token detected on database. Check the logic related to saving refresh token on database. ===");
            }

            // database 에서 해당 refreshToken 을 모두 삭제합니다.
            tokenRepository.deleteAllByToken(refreshToken);

            // 클라이언트에게는 상태코드 200 과 함께 정상적으로 처리되었음을 알립니다.
            try {
                responseWithBody.jsonResponse(response, 200, "logout succeeded.");
            } catch (IOException e) {
                log.info(e.getMessage());
            }
        }
        /*
        * database 에서 리프레시 토큰을 통해 해당 토큰을 찾고
        * 토큰이 null 이라면 클라이언트에게 상태 코드 400 과 함께 잘못된 접근임을 응답.
        * 토큰이 있다면, database 에서 해당 토큰을 삭제하고,
        * 클라이언트에게 상태코드 200 과 함께 로그인이 정상적으로 수행 되었으믈 전달.
        *
        * database 에서 토큰을 찾는데 2 개 이상의 토큰이 조회될 경우,
        *   refreshToken 발급과 저장 로직에 문제가 있는 것.
        *       로그를 남기고, 조속히 문제를 해결할 수 있도록 해야함.
        *
        * 대안. (애초에 database 에서 토큰을 찾을 떄, findAllByToken, deleteAllByToken 을 하는 것은?)
        *
        * 대안을 수행한다면, 토큰 개체가 2 이상 조회되면,
        *   개발자에게 refreshToken 저장 로직을 살펴볼 것을 알리는 로그를 남긴다?
        * */
        /*try {
            Token storedToken = tokenRepository.findByToken(refreshToken).orElse(null);
            if (storedToken == null) {

                log.info("user tried refresh token that does not exist on database.");

                try {
                    responseWithBody.jsonResponse(response, 400, null);
                } catch (IOException e) {
                    log.info(e.getMessage());
                }
                // refreshToken entity 가 존재한다면
            } else {

                // database 에서 해당 refreshToken 을 삭제합니다.
                tokenRepository.delete(storedToken);

                // 클라이언트에게는 상태코드 200 과 함께 정상적으로 처리되었음을 알립니다.
                try {
                    responseWithBody.jsonResponse(response, 200, null);
                } catch (IOException e) {
                    log.info(e.getMessage());
                }
            }

        } catch (IncorrectResultSizeDataAccessException e) {
            log.info(" === repository method exception has been occurred, detail is: {} ===", e.getMessage());
        }*/
    }
}
