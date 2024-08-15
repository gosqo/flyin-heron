package com.vong.manidues.dto.comment;

import com.vong.manidues.domain.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentPageResponse {
    private Slice<CommentGetResponse> commentPage;

    public static CommentPageResponse of(Slice<Comment> entityPage) {
        return CommentPageResponse.builder()
                .commentPage(new SliceImpl<>(
                        entityPage.get().map(CommentGetResponse::of).toList()
                        , entityPage.getPageable()
                        , entityPage.hasNext()
                ))
                .build();
    }
}
