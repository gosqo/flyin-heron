package com.vong.manidues.board.dto;

import com.vong.manidues.board.Board;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BoardDTO {


    private String title;
    private String content;
    private String writer;
    private LocalDateTime registerDate;
    private LocalDateTime updateDate;

    public BoardDTO(Board entity) {
        this.title = entity.getTitle();
        this.content = entity.getContent();
        this.writer = entity.getMember().getNickname();
        this.registerDate = entity.getRegisterDate();
        this.updateDate = entity.getUpdateDate();
    }

    public void fromEntity(Board entity) {
        this.title = entity.getTitle();
        this.content = entity.getContent();
        this.writer = entity.getMember().getNickname();
        this.registerDate = entity.getRegisterDate();
        this.updateDate = entity.getUpdateDate();
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
