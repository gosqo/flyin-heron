package com.vong.manidues.token;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenScheduler {
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;

    @Scheduled(cron = "${schedule.cron.token.delete-expired}")
    public void deleteExpiredTokens() {
        log.info("TokenScheduler#deleteExpiredTokens() called.");
        List<Token> tokens = tokenRepository.findAll();
        tokens.stream()
                .map(Token::getToken)
                .filter(token -> {
                    try {
                        return jwtService.extractExpiration(token).before(new Date(System.currentTimeMillis()));
                    } catch (JwtException e) {
                        return true;
                    }
                })
                .forEach(tokenRepository::deleteByToken);
    }
}
