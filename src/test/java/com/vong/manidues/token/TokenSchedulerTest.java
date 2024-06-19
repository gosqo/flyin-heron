package com.vong.manidues.token;

import com.vong.manidues.member.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.vong.manidues.member.MemberUtility.buildMockMember;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
        , properties = {
                "schedule.cron.token.delete-expired=0/1 * * * * *"
        }
)
@ActiveProfiles("test")
@Slf4j
class TokenSchedulerTest {
    private final TokenRepository tokenRepository;
    private final TokenUtility tokenUtility;
    private final EntityManager entityManager;

    @Autowired
    public TokenSchedulerTest(
            TokenRepository tokenRepository
            , TokenUtility tokenUtility
            , EntityManager entityManager
    ) {
        this.tokenRepository = tokenRepository;
        this.tokenUtility = tokenUtility;
        this.entityManager = entityManager;
    }

    @BeforeEach
    void insertTokens() {
        Member member = buildMockMember();
        List<String> tokens = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            long expiration = -i - 1;

            String token = tokenUtility.buildToken(new HashMap<>(), member, expiration);
            tokens.add(token);
        }

        for (String token : tokens) {
            tokenRepository.save(
                    Token.builder()
                            .member(member)
                            .token(token)
                            .build()
            );
        }

        Query query = entityManager.createNativeQuery("SELECT * FROM token t", Token.class);
        List<?> storedTokens = query.getResultList();
        storedTokens.forEach(i -> log.info(i.toString()));
    }

    @AfterEach
    void tearDown() {
        Query query = entityManager.createNativeQuery("SELECT * FROM token tok", Token.class);
        List<?> storedTokens = query.getResultList();
        storedTokens.forEach(i -> log.info(i.toString()));
        if (storedTokens.isEmpty()) log.info("tokens been delete.");
    }

    @Test
    void deleteExpiredTokens() throws InterruptedException {
        Thread.sleep(1500);
    }
}