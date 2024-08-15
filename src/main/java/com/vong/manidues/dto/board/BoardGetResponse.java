package com.vong.manidues.dto.board;

import com.vong.manidues.domain.Board;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class BoardGetResponse {

    private Long boardId;
    private Long writerId;
    private String title;
    private String content;
    private String writer;
    private Long viewCount;
    private LocalDateTime registerDate;
    private LocalDateTime updateDate;

    public static BoardGetResponse of(Board entity) {
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
}
