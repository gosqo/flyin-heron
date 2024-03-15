package com.vong.manidues.board;

import com.vong.manidues.board.dto.BoardRegisterRequest;
import com.vong.manidues.board.dto.BoardRegisterResponse;
import com.vong.manidues.config.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/board")
@RequiredArgsConstructor
@Slf4j
public class BoardController {

    private final JwtService jwtService;
    private final BoardService service;
//    private final HttpServletRequest servletRequest;
//    String authHeader = servletRequest.getHeader("Authorization");
//    String accessToken = authHeader.substring(7);
//    String requestUserEmail = jwtService.extractUserEmail(accessToken);

    @PostMapping("")
    public ResponseEntity<BoardRegisterResponse> registerBoard(
            HttpServletRequest servletRequest,
            @RequestBody BoardRegisterRequest request
    ) {
        String authHeader = servletRequest.getHeader("Authorization");
        String accessToken = authHeader.substring(7);
        String requestUserEmail = jwtService.extractUserEmail(accessToken);

        log.info("request to POST /api/v1/board/ Email is : {}", requestUserEmail);

        return ResponseEntity.ok(
            BoardRegisterResponse.builder()
                    .boardId(service.register(requestUserEmail, request))
                    .build()
        );
    }
}
