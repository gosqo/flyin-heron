package com.gosqo.flyinheron.acceptance;

import com.gosqo.flyinheron.domain.Token;
import com.gosqo.flyinheron.global.data.TestDataRemover;
import com.gosqo.flyinheron.repository.MemberRepository;
import com.gosqo.flyinheron.repository.TokenRepository;
import com.gosqo.flyinheron.service.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
        , properties = {"schedule.cron.token.delete-expired=0/1 * * * * *"}
)
@Slf4j
class TokenSchedulerTest extends SpringBootTestBase {
    private final JwtService jwtService;
    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;

    @Autowired
    public TokenSchedulerTest(
            TestRestTemplate template
            , JwtService jwtService
            , MemberRepository memberRepository
            , TokenRepository tokenRepository
            , TestDataRemover remover
    ) {
        super(template, remover);
        this.jwtService = jwtService;
        this.memberRepository = memberRepository;
        this.tokenRepository = tokenRepository;
    }

    @BeforeEach
    void setUp() {
        member = memberRepository.save(buildMember());
    }

    private Map<String, Date> buildTestTokens() {
        Map<String, Date> testTokens = new HashMap<>();
        int tokenCount = 3;
        int lastTokenIndex = tokenCount - 1;

        IntStream.range(0, tokenCount).forEach(i -> {
            long expiration = -i * 1_000_000L - 1_000_000L;

            if (i == lastTokenIndex) {
                expiration *= -1; // expiration 미래로 설정.
            }

            Date expirationDate = new Date(System.currentTimeMillis() + expiration);
            String token = jwtService.generateRefreshToken(member, expiration);

            testTokens.put(token, expirationDate);
        });

        return testTokens;
    }

    private void persistMappedTokens(Map<String, Date> testTokens) {
        for (String token : testTokens.keySet()) {
            tokenRepository.save(Token.builder()
                    .member(member)
                    .token(token)
                    .expirationDate(testTokens.get(token)) // dummy Date to save expired token
                    .build());
        }
    }

    @Test
    void delete_expired_tokens() throws InterruptedException {
        persistMappedTokens(buildTestTokens());

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            List<Token> leftTokens = tokenRepository.findAll();
            assertThat(leftTokens.size()).isEqualTo(1);
        });
    }
}