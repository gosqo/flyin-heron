package com.vong.manidues.board;

import com.vong.manidues.board.dto.*;
import com.vong.manidues.utility.ServletRequestUtility;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/board")
@RequiredArgsConstructor
@Slf4j
public class BoardController {

    private final BoardService service;
    private final ServletRequestUtility servletRequestUtility;

    @GetMapping("/{id}")
    public ResponseEntity<BoardGetResponse> getBoard(
            @PathVariable("id") Long id
    ) {
        Board entity = service.get(id);

        return entity != null
                ? ResponseEntity.ok(new BoardGetResponse().fromEntity(entity))
                : ResponseEntity.status(404).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BoardDeleteResponse> deleteBoard(
            HttpServletRequest servletRequest,
            @PathVariable("id") Long id
    ) {
        String requestUserEmail = servletRequestUtility
                .extractEmailFromHeader(servletRequest);

        return service.delete(id, requestUserEmail)
                ? ResponseEntity.ok(
                        BoardDeleteResponse.builder()
                                .isDeleted(true)
                                .message("삭제되었습니다.")
                                .build()
                )
                : ResponseEntity.status(400).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<BoardUpdateResponse> updateBoard(
            HttpServletRequest servletRequest,
            @PathVariable("id") Long id,
            @RequestBody BoardUpdateRequest request
    ) {
        String requestUserEmail = servletRequestUtility
                .extractEmailFromHeader(servletRequest);

        return service.update(id, requestUserEmail, request)
                ? ResponseEntity.ok(
                        BoardUpdateResponse.builder()
                                .id(id)
                                .isUpdated(true)
                                .message("해당 게시물의 수정이 처리됐습니다.")
                                .build()
                )
                : ResponseEntity.status(400).build();
    }

    @PostMapping("")
    public ResponseEntity<BoardRegisterResponse> registerBoard(
            HttpServletRequest servletRequest,
            @Valid @RequestBody BoardRegisterRequest request
    ) {
        String requestUserEmail = servletRequestUtility
                .extractEmailFromHeader(servletRequest);
        Long id = service.register(requestUserEmail, request);

        return id != null
                ? ResponseEntity.ok(
                        BoardRegisterResponse.builder()
                                .id(id)
                                .posted(true)
                                .message("게시물 등록이 완료됐습니다.")
                                .build()
                )
                : ResponseEntity.status(400).build();
    }
}
