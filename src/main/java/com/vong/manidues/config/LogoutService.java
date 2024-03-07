package com.vong.manidues.config;

import com.vong.manidues.token.JwtExceptionResponse;
import com.vong.manidues.token.TokenRepository;
import com.vong.manidues.utility.CustomServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogoutService implements LogoutHandler {

    private final TokenRepository tokenRepository;
    private final CustomServletResponse customServletResponse;

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

        // 쿼리 조회 결과 레코드 2 개 이상 나오면 예외 발생
        tokenRepository.findByToken(refreshToken).ifPresentOrElse( // org.springframework.dao.IncorrectResultSizeDataAccessException: Query did not return a unique result: 2 results were returned
                tokenRepository::delete,
                () -> {
                    try {
                        customServletResponse.jsonResponse(response, 400,
                                JwtExceptionResponse.builder()
                                        .exceptionMessage("인증 정보에 문제가 있습니다.")
                                        .build()
                        );
                        // todo ifPresentOrElse, Else 타고 나서 그 다음 try 를 또 타는 문제.
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        try {
            customServletResponse.jsonResponse(response, 200,
                    LogoutResponse.builder()
                            .result("success")
                            .build()
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
