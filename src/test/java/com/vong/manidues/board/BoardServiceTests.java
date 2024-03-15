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

        Long id = 20L;

        boolean hasUpdated = service.update(id, "heron@vong.com",
                BoardUpdateRequest.builder()
                        .title("updated again.")
                        .content("content been updated again.")
                        .build());

        log.info("update performed: {}", hasUpdated);
    }
}
