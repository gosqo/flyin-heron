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

    private Long boardId;
    private Long writerId;
    private String title;
    private String content;
    private String writer;
    private Long viewCount;
    private LocalDateTime registerDate;
    private LocalDateTime updateDate;

    public BoardGetResponse fromEntity(Board entity) {
        return BoardGetResponse.builder()
                .boardId(entity.getId())
                .writerId(entity.getMember().getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .writer(entity.getMember().getNickname())
                .viewCount(entity.getViewCount())
                .registerDate(entity.getRegisterDate())
                .updateDate(entity.getUpdateDate())
                .build();
    }

    @Override
    public String toString() {
        return String.format("""
                        BoardGetResponse{
                            boardId = %d,
                            writerId = %d,
                            title = "%s",
                            content = "%s",
                            writer = "%s"
                            viewCount = %d,
                            registerDate = '%s',
                            updateDate = '%s',
                        """
                , boardId, writerId, title, content, writer
                , viewCount, registerDate.toString(), updateDate.toString());
    }
}
