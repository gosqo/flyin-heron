package com.gosqo.flyinheron.global.data;

import com.gosqo.flyinheron.domain.*;
import com.gosqo.flyinheron.domain.member.Role;
import com.gosqo.flyinheron.repository.jpaentity.MemberProfileImageJpaEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static com.gosqo.flyinheron.domain.fixture.MemberFixture.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Slf4j
public abstract class TestDataInitializer {
    protected static final int BOARD_COUNT = 3;
    protected static final int COMMENT_COUNT = 20;
    protected static final int COMMENT_LIKE_COUNT = COMMENT_COUNT / 2;

    protected Member member;
    protected MemberProfileImage profileImage;
    protected MemberProfileImageJpaEntity profileImageJpaEntity;
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

    protected MemberProfileImage buildProfileImage() {
        File sampleImage = TestImageCreator.createTestImage(100, 100, "TestDataInitializer built it");

        return MemberProfileImage.convertToProfileImage(member.toModel(), sampleImage);
    }

    protected MemberProfileImage buildProfileImage(String filename) {
        File sampleImage = TestImageCreator.createTestImage(100, 100, filename);

        return MemberProfileImage.convertToProfileImage(member.toModel(), sampleImage);
    }

    protected MemberProfileImageJpaEntity buildProfileImageJpaEntity() {
        MemberProfileImage image = buildProfileImage();

        image.saveLocal();

        return MemberProfileImageJpaEntity.of(image);
    }

    protected MemberProfileImageJpaEntity buildProfileImageJpaEntity(String filename) throws IOException {
        MemberProfileImage image = buildProfileImage(filename);

        image.saveLocal();

        return MemberProfileImageJpaEntity.of(image);
    }

    protected List<Board> buildBoards() {
        List<Board> boards = new ArrayList<>();

        IntStream.range(0, BOARD_COUNT).forEach(
                i -> boards.add(
                        Board.builder()
                                .member(member)
                                .title("Board title " + (i + 1))
                                .content("Board content " + (i + 1))
                                .build()
                )
        );

        return boards;
    }

    protected List<Comment> buildComments() {
        List<Comment> comments = new ArrayList<>();

        IntStream.range(0, COMMENT_COUNT).forEach(
                i -> comments.add(
                        Comment.builder()
                                .member(member)
                                .board(boards.get(0))
                                .content("Hello, Comment " + (i + 1))
                                .build())
        );

        return comments;
    }

    protected List<CommentLike> buildCommentLikes() {
        List<CommentLike> commentLikes = new ArrayList<>();

        // 테스트를 위해 생성된 전체 comments 중, 초기 절반의 comments 에 멤버가 좋아요 함.
        IntStream.range(0, COMMENT_LIKE_COUNT).forEach(
                i -> commentLikes.add(
                        CommentLike.builder()
                                .member(member)
                                .comment(comments.get(i))
                                .build()
                )
        );

        return commentLikes;
    }
}
