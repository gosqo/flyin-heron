package com.gosqo.flyinheron.global.data;

import com.gosqo.flyinheron.domain.Board;
import com.gosqo.flyinheron.domain.Comment;
import com.gosqo.flyinheron.domain.CommentLike;
import com.gosqo.flyinheron.domain.Member;
import com.gosqo.flyinheron.domain.fixture.BoardFixture;
import com.gosqo.flyinheron.domain.member.Role;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static com.gosqo.flyinheron.domain.fixture.MemberFixture.*;

@ActiveProfiles("test")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Slf4j
public abstract class TestDataInitializer {
    protected static final int BOARD_COUNT = 3;
    protected static final int COMMENT_COUNT = 20;
    protected static final int COMMENT_LIKE_COUNT = COMMENT_COUNT / 2;

    protected Member member;
    protected List<Board> boards;
    protected List<Comment> comments;
    protected List<CommentLike> commentLikes;

    protected Member buildMember() {
        return Member.builder()
                .email(EMAIL)
                .nickname(NICKNAME)
                .password(ENCODED_PASSWORD)
                .role(Role.USER)
                .build();
    }

    protected List<Board> buildBoards() {
        List<Board> boards = new ArrayList<>();

        for (int i = 0; i < BOARD_COUNT; i++) {
            Board board = Board.builder()
                    .member(member)
                    .title(BoardFixture.TITLE + i + 1)
                    .content(BoardFixture.CONTENT + i + 1)
                    .build();
            boards.add(board);
        }
        return boards;
    }

    protected List<Comment> buildComments() {
        List<Comment> comments = new ArrayList<>();

        for (int i = 0; i < COMMENT_COUNT; i++) {
            Comment comment = Comment.builder()
                    .member(member)
                    .board(boards.get(0))
                    .content("Hello, Comment " + i + 1)
                    .build();
            comments.add(comment);
        }
        return comments;
    }

    protected List<CommentLike> buildCommentLikes() {
        List<CommentLike> commentLikes = new ArrayList<>();

        // 테스트를 위해 생성된 전체 comments 중, 초기 절반의 comments 에 멤버가 좋아요 함.
        IntStream.range(0, COMMENT_LIKE_COUNT)
                .forEach(
                        (i) -> {
                            commentLikes.add(
                                    CommentLike.builder()
                                            .member(member)
                                            .comment(comments.get(i))
                                            .build()
                            );
                        }
                );

        return commentLikes;
    }
}
