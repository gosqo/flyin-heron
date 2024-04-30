package com.vong.manidues.board;

import com.vong.manidues.board.dto.*;
import com.vong.manidues.utility.ServletRequestUtility;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/v1/board")
@RequiredArgsConstructor
@Slf4j
public class BoardController {

    private final BoardService service;
    private final ServletRequestUtility servletRequestUtility;

    private void checkViewed(
            HttpServletRequest request
            , HttpServletResponse response
            , Long id
    ) {
        // viewed flag, 쿠키에 해당 보드를 조회한 내역이 있는지. 반환 결과에 따라서 조회수 증가.
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equalsIgnoreCase("bbv")) { // bbv boardsBeenViewed
                    String beenViewed = cookie.getValue();
                    if (beenViewed.contains(id.toString())) return;
                    else beenViewed += "[" + id + "]";

                    cookie.setValue(beenViewed);
                    cookie.setAttribute("Expires", cookie.getAttribute("Expires"));
                    response.addCookie(cookie);
                    return;
                }
            }
        }
        Cookie newCookie = new Cookie("bbv", "[" + id.toString() + "]");
        newCookie.setMaxAge(60 * 60);
        response.addCookie(newCookie);

        Collection<String> responseHeaderNames = response.getHeaderNames();

        for (String headerName : responseHeaderNames) {
            log.info("{}: {}"
                    , headerName
                    , response.getHeader(headerName)
            );
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardGetResponse> getBoard(
            @PathVariable("id") Long id
            , HttpServletRequest request
            , HttpServletResponse response
    ) {
        checkViewed(request, response, id);

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
