package com.vong.manidues.board;

import com.vong.manidues.board.dto.BoardDTO;
import com.vong.manidues.board.dto.BoardGetResponse;
import com.vong.manidues.member.Member;
import com.vong.manidues.member.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Slf4j
public class BoardRepositoryTests {

    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    public void registerBoard() {
        Member member = memberRepository.findById(1L).orElseThrow();

        boardRepository.save(
            Board.builder()
                    .title("Hello world.")
                    .member(member)
                    .content("This is a test post on manidues.")
                    .build()
        );
    }

    @Test
    public void getBoard() {
        Board board = boardRepository.findById(6L).orElseThrow();
        log.info("selected board is: {}", board);
        BoardGetResponse response = new BoardGetResponse();
        response.fromEntity(board);

        log.info("responseEntityBody is: {}", response);

    }

    @Test
    public void getBoardListByWriter() {
        List<Board> boardList = boardRepository.findAllByWriter("greyHeron").stream().toList();
        List<BoardDTO> boardDTOList = new ArrayList<BoardDTO>();

        for (Board board : boardList) {
            BoardDTO dto = new BoardDTO(board);
            boardDTOList.add(dto);
        }
        log.info("selected boards are: {}", boardDTOList);
    }

    @Test
    public void getBoardListByKeywordLikeWriter() {
        List<Board> boardList = boardRepository.findAllByKeywordLikeWriter("greyH").stream().toList();
        List<BoardDTO> boardDTOList = new ArrayList<BoardDTO>();

        for (Board board : boardList) {
            BoardDTO dto = new BoardDTO(board);
            boardDTOList.add(dto);
        }
        log.info("selected boards are: {}", boardDTOList);
    }

 @Test
    public void getBoardListByKeywordLikeTitle() {
        List<Board> boardList = boardRepository.findAllByKeywordLikeTitle("Hello").stream().toList();
        List<BoardDTO> boardDTOList = new ArrayList<BoardDTO>();

        for (Board board : boardList) {
            BoardDTO dto = new BoardDTO(board);
            boardDTOList.add(dto);
        }
        log.info("selected boards are: {}", boardDTOList);
    }

 @Test
    public void getBoardListByKeywordLikeContent() {
        List<Board> boardList = boardRepository.findAllByKeywordLikeContent("this").stream().toList();
        List<BoardDTO> boardDTOList = new ArrayList<BoardDTO>();

        for (Board board : boardList) {
            BoardDTO dto = new BoardDTO(board);
            boardDTOList.add(dto);
        }
        log.info("selected boards are: {}", boardDTOList);
    }

}
