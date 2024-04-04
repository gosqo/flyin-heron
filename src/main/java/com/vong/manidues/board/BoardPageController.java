package com.vong.manidues.board;

import com.vong.manidues.board.dto.BoardPageResponse;
import com.vong.manidues.utility.ServletRequestUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/boards")
@RequiredArgsConstructor
@Slf4j
public class BoardPageController {

    private final BoardService service;
    private final ServletRequestUtility servletRequestUtility;

    @GetMapping("/{pageNumber}")
    public ResponseEntity<BoardPageResponse> getBoardList(
            @PathVariable("pageNumber") int pageNumber
    ) {
        pageNumber = pageNumber - 1;
        int pageSize = 3;
        Sort sort = Sort.by(Sort.Direction.DESC, "registerDate");

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort);

        Page<Board> foundPage = service.getBoardPage(pageRequest);

        return foundPage != null
                ? ResponseEntity.status(200).body(new BoardPageResponse()
                        .fromEntityPage(foundPage))
                : ResponseEntity.status(404).build();
    }
}
