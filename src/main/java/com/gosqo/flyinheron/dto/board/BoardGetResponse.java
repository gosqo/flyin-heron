package com.gosqo.flyinheron.dto.board;

import com.gosqo.flyinheron.domain.Board;
import com.gosqo.flyinheron.dto.member.MemberLightInfo;
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
    private MemberLightInfo member;
    private String title;
    private String content;
    private String writer;
    private Long viewCount;
    private LocalDateTime registerDate;
    private LocalDateTime updateDate;

    public static BoardGetResponse of(Board entity) {
        MemberLightInfo memberInfo = MemberLightInfo.of(entity.getMember().toModel());

        return BoardGetResponse.builder()
                .boardId(entity.getId())
                .member(memberInfo)
                .title(entity.getTitle())
                .content(entity.getContent())
                .viewCount(entity.getViewCount())
                .registerDate(entity.getRegisterDate())
                .updateDate(entity.getUpdateDate())
                .build();
    }
}
