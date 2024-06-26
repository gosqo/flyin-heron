package com.vong.manidues.token;

import io.jsonwebtoken.ExpiredJwtException;
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
                .filter(this::isTokenExpired)
                .forEach(tokenRepository::deleteByToken);
    }

    protected boolean isTokenExpired(String token) {
        try {
            return jwtService.extractExpiration(token).before(new Date(System.currentTimeMillis()));
        } catch (ExpiredJwtException e) {
            return true;
        } catch (JwtException e) {
            log.warn("{} occurred while checking expiration of token on database, thrown by token: {}", e.getClass().getSimpleName(), token);
            return false;
        } catch (RuntimeException e) {
            log.warn("{} occurred while checking expiration of token on database, thrown by token: {}", e.getClass().getName(), token);
            return false;
        }
    }
}
