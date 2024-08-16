package com.vong.manidues.integrated;

import com.vong.manidues.global.data.RepositoryDataInitializer;
import com.vong.manidues.repository.BoardRepository;
import com.vong.manidues.repository.CommentRepository;
import com.vong.manidues.repository.MemberRepository;
import com.vong.manidues.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class SpringBootTestBase extends RepositoryDataInitializer {
    protected final TestRestTemplate template;

    @Autowired
    public SpringBootTestBase(
            MemberRepository memberRepository,
            BoardRepository boardRepository,
            CommentRepository commentRepository,
            TokenRepository tokenRepository,
            TestRestTemplate template
    ) {
        super(memberRepository, boardRepository, commentRepository, tokenRepository);
        this.template = template;
    }
}
