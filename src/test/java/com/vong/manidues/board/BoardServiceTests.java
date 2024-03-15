package com.vong.manidues.board;

import com.vong.manidues.board.dto.BoardUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class BoardServiceTests {

    @Autowired
    private BoardService service;

    @Test
    public void updateBoard() {

        boolean hasUpdated = service.update(BoardUpdateRequest.builder()
                .id(20L)
                .title("updated.")
                .content("content been updated.")
                .build()
        );

        log.info("update performed: {}", hasUpdated);
    }
}
