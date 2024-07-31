package com.vong.manidues.domain.token;

import com.vong.manidues.domain.board.BoardRepository;
import com.vong.manidues.domain.comment.CommentRepository;
import com.vong.manidues.domain.integrated.SpringBootTestBase;
import com.vong.manidues.domain.member.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
        , properties = {"schedule.cron.token.delete-expired=0/1 * * * * *"}
)
@Slf4j
class TokenSchedulerTest extends SpringBootTestBase {
    private final TokenUtility tokenUtility;

    @Autowired
    public TokenSchedulerTest(
            MemberRepository memberRepository,
            TokenRepository tokenRepository,
            BoardRepository boardRepository,
            CommentRepository commentRepository,
            TestRestTemplate template,
            TokenUtility tokenUtility
    ) {
        super(memberRepository, tokenRepository, boardRepository, commentRepository, template);
        this.tokenUtility = tokenUtility;
    }

    @BeforeEach
    void setUp() {
        initMember();
    }

    private void saveTokens() {
        List<String> tokens = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            long expiration = -i * 1_000_000L - 1_000_000L;

            if (i == 2) expiration = -expiration;
            String token = tokenUtility.buildToken(new HashMap<>(), member, expiration);
            tokens.add(token);
        }

        for (String token : tokens) {
            tokenRepository.save(Token.builder().member(member).token(token).expirationDate(new Date()) // dummy
                    .build());
        }
    }

    private void logStoredTokens() {
        tokenUtility.getStoredTokens().forEach(i -> log.info(i.toString()));
    }

    @Test
    void deleteExpiredTokens() throws InterruptedException {
        saveTokens();
        logStoredTokens();

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            List<Token> leftTokens = tokenRepository.findAll();
            assertThat(leftTokens.size()).isEqualTo(1);
        });

        logStoredTokens();
//        tokenRepository.deleteAll();
//        memberRepository.deleteAll();
    }
}