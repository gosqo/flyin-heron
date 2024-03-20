package com.vong.manidues.board;

import com.vong.manidues.board.dto.BoardRegisterRequest;
import com.vong.manidues.board.dto.BoardUpdateRequest;
import com.vong.manidues.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;

    @Override
    public Page<Board> getBoardPage(Pageable pageable) {
        return boardRepository.findAll(pageable);
    }

    @Override
    public Long register(String userEmail, BoardRegisterRequest request) {

        if (memberRepository.findByEmail(userEmail).isPresent()) {

            Board entity = request.toEntity(memberRepository.findByEmail(userEmail).get());
            return boardRepository.save(entity).getId();

        } else {

            throw new NoSuchElementException("No Member present with the email.");
        }
    }

    @Override
    public boolean update(Long id,
                          String requestUserEmail,
                          BoardUpdateRequest request) {

        Board storedBoard = boardRepository.findById(id).orElseThrow();

        if (storedBoard.getMember().getEmail().equals(requestUserEmail)) {

            storedBoard.updateTitle(request.getTitle());
            storedBoard.updateContent(request.getContent());

            boardRepository.save(storedBoard);

            return true;
        }

        return false;
    }

    @Override
    public boolean delete(Long id, String requestUserEmail) {

        Board storedBoard = boardRepository.findById(id).orElseThrow();

        if (storedBoard.getMember().getEmail().equals(requestUserEmail)) {

            boardRepository.delete(storedBoard);
            return true;
        }

        return false;
    }

    @Override
    public Board get(Long id) {
        return boardRepository.findById(id).orElse(null);
    }
}
