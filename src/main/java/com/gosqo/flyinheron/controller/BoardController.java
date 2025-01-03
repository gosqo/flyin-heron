package com.gosqo.flyinheron.controller;

import com.gosqo.flyinheron.dto.board.*;
import com.gosqo.flyinheron.service.BoardService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestController
@RequestMapping("/api/v1/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService service;

    @GetMapping("")
    public ResponseEntity<BoardPageResponse> getBoardList(
            @RequestParam("page") int pageNumber
    ) throws NoResourceFoundException {
        return ResponseEntity.status(200).body(service.getBoardPage(pageNumber));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardGetResponse> getBoard(
            @PathVariable("id") Long id
            , HttpServletRequest request
            , HttpServletResponse response
    ) throws NoResourceFoundException {
        return ResponseEntity
                .status(200)
                .body(service.get(id, request, response));
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<BoardDeleteResponse> deleteBoard(
            HttpServletRequest request,
            @PathVariable("id") Long id
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.delete(id, request));
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("/{id}")
    public ResponseEntity<BoardUpdateResponse> updateBoard(
            HttpServletRequest request,
            @PathVariable("id") Long id,
            @Validated @RequestBody BoardUpdateRequest body
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.update(id, request, body));
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("")
    public ResponseEntity<BoardRegisterResponse> registerBoard(
            HttpServletRequest request,
            @Validated @RequestBody BoardRegisterRequest requestBody
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.register(request, requestBody));
    }
}
