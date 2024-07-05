package com.vong.manidues.token;

import com.vong.manidues.member.Member;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.vong.manidues.member.MemberUtility.buildMockMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
        , properties = {
        "schedule.cron.token.delete-expired=0/1 * * * * *"
}
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
@Slf4j
class TokenSchedulerTest {
    private final TokenRepository tokenRepository;
    private final TokenUtility tokenUtility;
    private final TokenScheduler tokenScheduler;

    @Autowired
    public TokenSchedulerTest(
            TokenRepository tokenRepository
            , TokenUtility tokenUtility
            , TokenScheduler tokenScheduler
    ) {
        this.tokenRepository = tokenRepository;
        this.tokenUtility = tokenUtility;
        this.tokenScheduler = tokenScheduler;
    }

    @BeforeEach
    void setUp() {
        Member member = buildMockMember();
        List<String> tokens = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            long expiration = -i * 1_000_000L - 1_000_000L;

            if (i == 2) expiration = -expiration;
            String token = tokenUtility.buildToken(new HashMap<>(), member, expiration);
            tokens.add(token);
        }

        for (String token : tokens) {
            tokenRepository.save(
                    Token.builder()
                            .member(member)
                            .token(token)
                            .expirationDate(new Date()) // dummy
                            .build()
            );
        }

        logStoredTokens();
    }

    @AfterEach
    void tearDown() {
        logStoredTokens();
    }

    private void logStoredTokens() {
        tokenUtility.getStoredTokens().forEach(i -> log.info(i.toString()));
    }

    @Test
    void deleteExpiredTokens() throws InterruptedException {
        await().atMost(2, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    List<Token> leftTokens = tokenRepository.findAll();
                    assertThat(leftTokens.size()).isEqualTo(1);
                });
    }
}