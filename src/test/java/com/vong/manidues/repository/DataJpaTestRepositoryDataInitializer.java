package com.vong.manidues.repository;

import com.vong.manidues.global.config.JpaAuditingConfig;
import com.vong.manidues.global.data.RepositoryDataInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest(showSql = false)
@Import(JpaAuditingConfig.class)
abstract class DataJpaTestRepositoryDataInitializer extends RepositoryDataInitializer {

    @Autowired
    public DataJpaTestRepositoryDataInitializer(
            MemberRepository memberRepository,
            BoardRepository boardRepository,
            CommentRepository commentRepository,
            TokenRepository tokenRepository
    ) {
        super(memberRepository, boardRepository, commentRepository, tokenRepository);
    }
}
