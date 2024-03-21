package com.vong.manidues.board;

import com.vong.manidues.board.dto.BoardUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@SpringBootTest
@Slf4j
public class BoardServiceTests {

    @Autowired
    private BoardService service;

    @Test
    public void getBoardPage() {
        int pageNumber = 0;
        int pageSize = 6;
        Sort sort = Sort.by(Sort.Direction.DESC, "registerDate");

        PageRequest request = PageRequest.of(pageNumber, pageSize, sort);

        Page<Board> boardPage = service.getBoardPage(request);

        log.info("{}", boardPage);
        log.info("{}", boardPage.getTotalPages());
        log.info("{}", boardPage.getContent());
        log.info("{}", boardPage.getTotalElements());
        log.info("{}", boardPage.getNumber());
        log.info("{}", boardPage.getNumberOfElements());
        log.info("{}", boardPage.getPageable());
    }

    @Test
    public void updateBoard() {

        Long id = 20L;

        boolean hasUpdated = service.update(id, "heron@vong.com",
                BoardUpdateRequest.builder()
                        .title("updated again.")
                        .content("content been updated again.")
                        .build());

        log.info("update performed: {}", hasUpdated);
    }
}
