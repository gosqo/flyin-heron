package com.vong.manidues.board;

import com.vong.manidues.board.dto.BoardRegisterRequest;
import com.vong.manidues.board.dto.BoardUpdateRequest;
import org.springframework.stereotype.Service;

@Service
public interface BoardService {

    public Long register(String userEmail, BoardRegisterRequest request);

    public boolean update(Long id,
                          String requestUserEmail,
                          BoardUpdateRequest request);

    public boolean delete(Long id, String requestUserEmail);

    Board get(Long id);
}
