package com.vong.manidues.board;

import com.vong.manidues.board.dto.*;
import com.vong.manidues.utility.AuthHeaderUtility;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/board")
@RequiredArgsConstructor
@Slf4j
public class BoardController {

    private final BoardService service;
    private final AuthHeaderUtility authHeaderUtility;

    @GetMapping("/{id}")
    public ResponseEntity<BoardGetResponse> getBoard(
            @PathVariable("id") Long id
            , HttpServletRequest request
            , HttpServletResponse response
    ) {
        return ResponseEntity.status(200).body(service.get(id, request, response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BoardDeleteResponse> deleteBoard(
            HttpServletRequest servletRequest,
            @PathVariable("id") Long id
    ) {
        String requestUserEmail = authHeaderUtility
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
            HttpServletRequest request,
            @PathVariable("id") Long id,
            @RequestBody BoardUpdateRequest body
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.update(id, request, body));
    }

    @PostMapping("")
    public ResponseEntity<BoardRegisterResponse> registerBoard(
            HttpServletRequest servletRequest,
            @Valid @RequestBody BoardRegisterRequest request
    ) {
        String requestUserEmail = authHeaderUtility
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
