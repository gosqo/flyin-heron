package com.vong.manidues.global.data;

import com.vong.manidues.repository.BoardRepository;
import com.vong.manidues.repository.CommentRepository;
import com.vong.manidues.repository.MemberRepository;
import com.vong.manidues.repository.TokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class RepositoryDataInitializer extends TestDataInitializer {
    protected final MemberRepository memberRepository;
    protected final TokenRepository tokenRepository;
    protected final BoardRepository boardRepository;
    protected final CommentRepository commentRepository;

    @Autowired
    public RepositoryDataInitializer(
            MemberRepository memberRepository
            , BoardRepository boardRepository
            , CommentRepository commentRepository
            , TokenRepository tokenRepository
    ) {
        this.memberRepository = memberRepository;
        this.boardRepository = boardRepository;
        this.commentRepository = commentRepository;
        this.tokenRepository = tokenRepository;
    }

    @AfterEach
    void tearDown() {
        log.info("==== Deleting test data. ====");
        commentRepository.deleteAll();
        boardRepository.deleteAll();
        tokenRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Override
    protected void initMember() {
        member = memberRepository.save(buildMember());
    }

    @Override
    protected void initBoards() {
        initMember();
        boards = boardRepository.saveAll(buildBoards());
    }

    @Override
    protected void initComments() {
        initBoards();
        comments = commentRepository.saveAll(buildComments());
    }
}
