package com.gosqo.flyinheron.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class CommentRegisterRequest {
    // wrapper class (기본형 자료를 객체로 다룸) 검증할 때는 @NotNull.
    // @NotBlank 를 사용하면 UnExpectedTypeException 발생.
    // 클래스에 적절한 검증도구를 붙이지 않았기 때문.
    @NotNull(message = "댓글이 참조하는 게시물이 없습니다.")
    private Long boardId;
    // String 에 @NotBlank, @NotEmpty, @NotNull 가능.
    @NotBlank(message = "내용을 입력해주세요.")
    private String content;
}
