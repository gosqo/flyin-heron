package com.vong.manidues.board;

import com.vong.manidues.board.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Service
public interface BoardService {
    Page<Board> getBoardPage(Pageable pageable);

    BoardRegisterResponse register(HttpServletRequest request, BoardRegisterRequest requestBody);

    BoardUpdateResponse update(Long id, HttpServletRequest request, BoardUpdateRequest body);

    BoardDeleteResponse delete(Long id, HttpServletRequest request);

    BoardGetResponse get(Long id, HttpServletRequest request, HttpServletResponse response) throws NoResourceFoundException;
}
