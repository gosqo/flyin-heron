package com.vong.manidues.domain.comment.dto;

import com.vong.manidues.domain.comment.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentGetResponse {
    private Long id;
    private Long writerId;
    private String writerNickname;
    private Long boardId;
    private String content;
    private Long likeCount;
    private LocalDateTime registerDate;
    private LocalDateTime updateDate;

    public static CommentGetResponse of(Comment entity) {
        return CommentGetResponse.builder()
                .id(entity.getId())
                .writerId(entity.getMember().getId())
                .writerNickname(entity.getMember().getNickname())
                .boardId(entity.getBoard().getId())
                .content(entity.getContent())
                .likeCount(entity.getLikeCount())
                .registerDate(entity.getRegisterDate())
                .updateDate(entity.getUpdateDate())
                .build();
    }
}
