package com.vong.manidues.global.data;

import com.vong.manidues.domain.Board;
import com.vong.manidues.domain.Comment;
import com.vong.manidues.domain.Member;
import com.vong.manidues.domain.fixture.BoardFixture;
import com.vong.manidues.domain.member.Role;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static com.vong.manidues.domain.fixture.MemberFixture.*;

@ActiveProfiles("test")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Slf4j
public abstract class TestDataInitializer {
    protected static final int BOARD_COUNT = 3;
    protected static final int COMMENT_COUNT = 20;

    protected Member member;
    protected List<Board> boards;
    protected List<Comment> comments;

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

    protected abstract void initMember();

    protected abstract void initBoards();

    protected abstract void initComments();
}
