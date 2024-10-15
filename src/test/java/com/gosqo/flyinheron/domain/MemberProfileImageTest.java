package com.gosqo.flyinheron.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.gosqo.flyinheron.domain.AbstractImageManagerTest.IMAGE_CLIENT_PATH;
import static com.gosqo.flyinheron.domain.MemberProfileImageManager.MEMBER_PROFILE_IMAGE_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberProfileImageTest {
    private static final String CLIENT_IMAGE_FILENAME = "profile image.png";

    private final Path source = Paths.get(IMAGE_CLIENT_PATH, CLIENT_IMAGE_FILENAME);
    private MemberProfileImage memberProfileImage;

    @BeforeEach
    void setUp() throws IOException {
        this.memberProfileImage = MemberProfileImage.builder()
                .manager(new MemberProfileImageManager())
                .memberId(1L)
                .inputStream(Files.newInputStream(source))
                .originalFilename(CLIENT_IMAGE_FILENAME)
                .build();
    }

    @Test
    void toEntity_before_save_throws_IllegalStateException() {
        assertThatThrownBy(() -> memberProfileImage.toEntity())
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void saveMemberProfileImage() throws IOException {
        memberProfileImage.saveMemberProfileImage();
        Path saved = Paths.get(memberProfileImage.getFileFullPath());

        assertThat(saved.toFile().exists()).isTrue();
    }

    @Test
    void toEntity_after_save() throws IOException {
        // given
        memberProfileImage.saveMemberProfileImage();

        // when
        MemberProfileImageJpaEntity entity = memberProfileImage.toEntity();

        // then
        assertThat(entity.getFileFullPath()).contains(
                MEMBER_PROFILE_IMAGE_PATH, "1", "/profile/"
                , CLIENT_IMAGE_FILENAME.replaceAll(" ", "-")
        );
    }

    @Test
    void remove_former_if_exists_then_save_new_one() throws IOException {
        // given
        String fullPath = memberProfileImage.saveMemberProfileImage();
        MemberProfileImage change = MemberProfileImage.builder()
                .manager(new MemberProfileImageManager())
                .memberId(1L)
                .inputStream(Files.newInputStream(source))
                .originalFilename("changed file.png")
                .fileFullPath(fullPath)
                .build();
        ;

        // when
        change.saveMemberProfileImage();

        // then
        assertThat(Paths.get(fullPath).toFile().exists()).isFalse();
        assertThat(Paths.get(change.getFileFullPath()).toFile().exists()).isTrue();
    }
}
