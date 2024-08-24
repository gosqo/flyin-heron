package com.vong.manidues.dto.commentlike;

import com.vong.manidues.domain.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;

@Getter
@NoArgsConstructor
public class GetCommentsLikedByResponse {
    private Slice<Comment> comments;

    @Builder
    public GetCommentsLikedByResponse(Slice<Comment> comments) {
        this.comments = comments;
    }
}
