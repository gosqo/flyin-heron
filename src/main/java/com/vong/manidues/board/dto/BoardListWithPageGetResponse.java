package com.vong.manidues.board.dto;

import com.vong.manidues.board.Board;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@ToString
public class BoardListWithPageGetResponse {

    private List<BoardGetResponse> boardList;

    public void fromEntityList(Page<Board> entityList) {

        List<BoardGetResponse> temporalList = new ArrayList<>();

        for (Board board : entityList) {
            BoardGetResponse dto = new BoardGetResponse();
            dto.fromEntity(board);
            temporalList.add(dto);
        }

        this.boardList = temporalList;
    }

}
