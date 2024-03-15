package com.vong.manidues.board.dto;

import com.vong.manidues.board.Board;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardGetResponse {

    private String title;
    private String content;
    private String writer;
    private LocalDateTime registerDate;
    private LocalDateTime updateDate;

    public BoardGetResponse fromEntity(Board entity) {
        return BoardGetResponse.builder()
                .title(entity.getTitle())
                .content(entity.getContent())
                .writer(entity.getMember().getNickname())
                .registerDate(entity.getRegisterDate())
                .updateDate(entity.getUpdateDate())
                .build();
    }

    @Override
    public String toString() {
        return "\nBoardGetResponse{" +
                "\n\ttitle = '" + title + '\'' +
                ", \n\tcontent = '" + content + '\'' +
                ", \n\twriter = '" + writer + '\'' +
                ", \n\tregisterDate = " + registerDate +
                ", \n\tupdateDate = " + updateDate +
                "\n}";
    }

}
