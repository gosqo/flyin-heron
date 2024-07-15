package com.vong.manidues.domain.comment;

import com.vong.manidues.DataJpaTestJpaRepositoryBase;
import com.vong.manidues.domain.board.BoardRepository;
import com.vong.manidues.domain.member.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Slf4j
class CommentRepositoryTest extends DataJpaTestJpaRepositoryBase {

    @Autowired
    public CommentRepositoryTest(MemberRepository memberRepository, BoardRepository boardRepository, CommentRepository commentRepository) {
        super(memberRepository, boardRepository, commentRepository);
    }

    @Test
    void findByBoard_BoardId() {
        Pageable pageable = PageRequest.of(0, 4, Sort.Direction.DESC, "id");
        Page<Comment> result = commentRepository.findByBoardId(
                1L, pageable
        );

        result.getContent().forEach(item -> log.info("{}", item));
    }
}