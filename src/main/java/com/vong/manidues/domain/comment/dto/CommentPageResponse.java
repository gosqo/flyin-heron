package com.vong.manidues.domain.comment.dto;

import com.vong.manidues.domain.comment.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentPageResponse {
    private Page<CommentGetResponse> commentPage;

    public static CommentPageResponse of(Page<Comment> entityPage) {
        return CommentPageResponse.builder()
                .commentPage(new PageImpl<>(
                        entityPage.get().map(CommentGetResponse::of).toList()
                ))
                .build();
    }
}
