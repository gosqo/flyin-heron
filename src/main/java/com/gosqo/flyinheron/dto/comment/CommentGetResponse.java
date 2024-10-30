package com.gosqo.flyinheron.dto.comment;

import com.gosqo.flyinheron.domain.Comment;
import com.gosqo.flyinheron.dto.member.MemberLightInfo;
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
    private MemberLightInfo member;
    private Long boardId;
    private String content;
    private Long likeCount;
    private LocalDateTime registerDate;
    private LocalDateTime updateDate;

    public static CommentGetResponse of(Comment entity) {
        MemberLightInfo memberInfo = entity.getMember().toModel().toLightInfo();

        return CommentGetResponse.builder()
                .id(entity.getId())
                .member(memberInfo)
                .boardId(entity.getBoard().getId())
                .content(entity.getContent())
                .likeCount(entity.getLikeCount())
                .registerDate(entity.getRegisterDate())
                .updateDate(entity.getUpdateDate())
                .build();
    }
}
