package com.vong.manidues.common;

import com.vong.manidues.config.trackingip.RequestTracker;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Controller
public class ViewController {

    @GetMapping("/")
    public String getHome(HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");
        String requestedRemoteAddress = request.getRemoteAddr();

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {

            RequestTracker.trackRequest(requestedRemoteAddress, request);
            log.info("""
                           
                                request to "/" from {} count is: {}
                                requested User-Agent is: {}
                                requested Locale is: {}
                            """,
                    requestedRemoteAddress, RequestTracker.getRequestCount(requestedRemoteAddress),
                    RequestTracker.getUserAgent(requestedRemoteAddress),
                    request.getLocale()
            );

            if (RequestTracker.getRequestCount(requestedRemoteAddress) > 10) {
                log.info("""
                               
                                    This client has more than 10 attempts to hit "/" in an hour.
                                    RequestTracker is like:
                                    {}
                                """,
                        RequestTracker.getRequestMap()
                );

            }

        }
        RequestTracker.clearExpiredRequests();

        return "index";
    }

    @GetMapping("/board/{id}")
    public String getBoard(@NotNull @PathVariable("id") Long id) {
        log.info("request to board id: {}", id);
        return "board/board";
    }

    @GetMapping("/board/{id}/modify")
    public String modifyBoard(@NotNull @PathVariable("id") Long id) {
        log.info("request to board id: {}", id);
        return "board/boardModify";
    }

    @GetMapping(value = {"/boards", "/boards/{pageNumber}"})
    public String getBoardListView(@Nullable @PathVariable("pageNumber") Integer pageNumber) {
        return "board/boardList";
    }

    @GetMapping("/board/new")
    public String newBoard() {
        return "board/boardNew";
    }
}
