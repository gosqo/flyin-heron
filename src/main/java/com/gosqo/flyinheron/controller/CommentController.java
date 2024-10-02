package com.gosqo.flyinheron.controller;

import com.gosqo.flyinheron.dto.comment.*;
import com.gosqo.flyinheron.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
public class CommentController {
    private final CommentService service;

    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("/comment/{id}")
    public ResponseEntity<CommentDeleteResponse> removeComment(
            @PathVariable("id") Long id
            , HttpServletRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.remove(id, request));
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("/comment/{id}")
    public ResponseEntity<CommentUpdateResponse> modifyComment(
            @PathVariable("id") Long id
            , HttpServletRequest request
            , @Valid @RequestBody CommentUpdateRequest requestBody
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.modify(id, request, requestBody));
    }

    @GetMapping("/board/{boardId}/comments")
    public ResponseEntity<Slice<CommentGetResponse>> getSliceOfComment(
            @PathVariable("boardId") Long boardId
            , @RequestParam("page-number") int pageNumber
    ) throws NoResourceFoundException {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.getSliceOfComments(boardId, pageNumber));
    }

    @GetMapping("/comment/{id}")
    public ResponseEntity<CommentGetResponse> getComment(@PathVariable("id") Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.get(id));
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/comment")
    public ResponseEntity<CommentRegisterResponse> registerComment(
            HttpServletRequest request
            , @Valid @RequestBody CommentRegisterRequest requestBody
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.register(request, requestBody));
    }
}
