package com.vong.manidues.dto.board;

import com.vong.manidues.domain.Board;
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

    public static BoardPageResponse of(Page<Board> entityPage) {
        return BoardPageResponse.builder()
                .boardPage(new PageImpl<>(
                        entityPage.get().map(BoardGetResponse::of).toList(),
                        entityPage.getPageable(),
                        entityPage.getTotalElements()
                ))
                .build();
    }
}
