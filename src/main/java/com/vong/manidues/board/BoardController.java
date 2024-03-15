package com.vong.manidues.board;

import com.vong.manidues.board.dto.*;
import com.vong.manidues.utility.ServletRequestUtility;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/board")
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
                ? ResponseEntity.ok(
                new BoardGetResponse().fromEntity(entity)
        )
                : ResponseEntity.notFound().build();
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
                : ResponseEntity.badRequest().build();
    }

    @PutMapping("/{id}")
    @PatchMapping("/{id}")
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
                        .build()
        )
                : ResponseEntity.badRequest().build();
    }

    @PostMapping("")
    public ResponseEntity<BoardRegisterResponse> registerBoard(
            HttpServletRequest servletRequest,
            @RequestBody BoardRegisterRequest request
    ) {
        String requestUserEmail = servletRequestUtility
                .extractEmailFromHeader(servletRequest);

        log.info("request to POST /api/v1/board/ Email is : {}", requestUserEmail);

        return ResponseEntity.ok(
                BoardRegisterResponse.builder()
                        .boardId(service.register(requestUserEmail, request))
                        .build()
        );
    }


}
