package com.gosqo.flyinheron.repository;

import com.gosqo.flyinheron.domain.Comment;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

@Slf4j
class CommentRepositoryTest extends RepositoryTestBase {

    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public CommentRepositoryTest(
            MemberRepository memberRepository,
            BoardRepository boardRepository,
            CommentRepository commentRepository
    ) {
        this.memberRepository = memberRepository;
        this.boardRepository = boardRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    void initData() {
        member = memberRepository.saveAndFlush(buildMember());
        boards = boardRepository.saveAll(buildBoards());
        comments = commentRepository.saveAll(buildComments());
    }

    @Override
    @BeforeEach
    void setUp() {
        initData();
        log.info("==== Test data initialized. ====");
    }

    @Test
    void findByBoardId() {
        Long boardId = boards.get(0).getId();
        Pageable pageable = PageRequest.of(0, 4, Sort.Direction.ASC, "id");
        Slice<Comment> found = commentRepository.findByBoardId(boardId, pageable);

        found.getContent().forEach(item -> log.info("{}", item));
    }
}
