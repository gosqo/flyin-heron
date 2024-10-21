package com.gosqo.flyinheron.domain;

import com.gosqo.flyinheron.repository.jpaentity.MemberProfileImageJpaEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.IntStream;

import static com.gosqo.flyinheron.domain.DefaultImageManagerTest.CLIENT_IMAGE_DIR;
import static com.gosqo.flyinheron.domain.MemberProfileImageManager.MEMBER_IMAGE_DIR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
class MemberProfileImageTest {
    private static final Long MEMBER_ID = 20L;
    private static final String CLIENT_IMAGE_FILENAME = "profile image.png";
    private static final String CLIENT_IMAGE_FILENAME2 = "profile image2.png";
    private static final Path SOURCE = Paths.get(CLIENT_IMAGE_DIR, CLIENT_IMAGE_FILENAME);
    private static final Path SOURCE2 = Paths.get(CLIENT_IMAGE_DIR, CLIENT_IMAGE_FILENAME2);
    private MemberProfileImage profileImage;

    MemberProfileImageTest() {
    }

    @BeforeEach
    void setUp() throws IOException {
        InputStream inputStream = Files.newInputStream(SOURCE);

        this.profileImage = MemberProfileImage.builder()
                .memberId(MEMBER_ID)
                .inputStream(inputStream)
                .originalFilename(SOURCE.getFileName().toString())
                .build();
    }

    @Test
    void toEntity_before_saveLocal_throws_IllegalStateException() throws IOException {
        assertThatThrownBy(() -> profileImage.toEntity(null))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void saveMemberProfileImage() throws IOException {
        String fullPath = profileImage.saveLocal();
        Path output = Paths.get(fullPath);

        assertThat(output.toFile().exists()).isTrue();
    }

    @Test
    void toEntity_after_save() throws IOException {
        // given
        profileImage.saveLocal();

        // when
        MemberProfileImageJpaEntity entity = profileImage.toEntity(null);

        // then
        assertThat(entity.getFullPath()).contains(
                MEMBER_IMAGE_DIR, "1", "/profile/"
                , CLIENT_IMAGE_FILENAME.replaceAll(" ", "-")
        );
    }

    @Test
    void remove_former_if_exists_then_save_new_one() throws IOException {
        // given
        // dummies to be deleted.
        Path dummy = Paths.get(CLIENT_IMAGE_DIR, CLIENT_IMAGE_FILENAME);
        IntStream.range(0, 5).forEach(i -> {
            try (InputStream stream = Files.newInputStream(dummy)) {
                DefaultImageManager.getInstance().saveLocal(
                        stream
                        , CLIENT_IMAGE_FILENAME.replace(".", i + ".")
                        , profileImage.getStorageDir()
                );
            } catch (IOException e) {
                log.info("IOException. occurred, check.");
            }
        });

        // when
        // 이전 5 장의 더미 파일 삭제 및 테스트 필드 profileImage 객체 필드 로컬 저장.
        String formerFullPath = profileImage.saveLocal();
        Path formerOutput = Paths.get(formerFullPath);

        // then
        assertThat(formerOutput.toFile().exists()).isTrue();

        // when
        InputStream newInputStream = Files.newInputStream(SOURCE2);

        MemberProfileImage newImage = MemberProfileImage.builder()
                .memberId(MEMBER_ID)
                .inputStream(newInputStream)
                .originalFilename(SOURCE2.getFileName().toString())
                .build();

        // 저장된 로컬 파일(formerOutput) 삭제 및 newImage 객체 필드 로컬 저장.
        String latterFullPath = newImage.saveLocal();
        Path latterOutput = Paths.get(latterFullPath);

        // then
        assertThat(formerOutput.toFile().exists()).isFalse();
        assertThat(latterOutput.toFile().exists()).isTrue();
    }
}
