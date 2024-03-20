package com.vong.manidues.board.dto;

import com.vong.manidues.board.Board;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class BoardListWithPageGetResponse {

    private List<BoardGetResponse> boardList;

//    public BoardListWithPageGetResponse toList(Page<BoardGetResponse> page) {
//        return BoardListWithPageGetResponse.builder()
//                .boardList(page.stream().toList())
//                .build();
//    }

    public BoardListWithPageGetResponse fromEntityList(List<Board> entityList) {

        List<BoardGetResponse> temporalList = new ArrayList<>();
        for (Board board : entityList) {
            temporalList.add(new BoardGetResponse().fromEntity(board));
        }

        return BoardListWithPageGetResponse.builder()
                .boardList(temporalList)
                .build();
    }
}