package com.vong.manidues.board.dto;

import com.vong.manidues.board.Board;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardPageResponse {

    private Page<BoardGetResponse> boardPage;

    public BoardPageResponse fromEntityPage(Page<Board> entityPage) {
        BoardGetResponse boardGetResponse = new BoardGetResponse();
        ;
        return BoardPageResponse.builder()
                .boardPage(new PageImpl<BoardGetResponse>(
                        entityPage.get().map(boardGetResponse::fromEntity).toList()
                        , entityPage.getPageable()
                        , entityPage.getTotalElements()
                        )
                )
                .build();
    }

}
