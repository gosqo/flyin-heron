package com.gosqo.flyinheron.domain;

import com.gosqo.flyinheron.global.data.TestDataInitializer;
import com.gosqo.flyinheron.global.data.TestImageCreator;
import com.gosqo.flyinheron.repository.jpaentity.MemberProfileImageJpaEntity;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
class MemberProfileImageTest extends TestDataInitializer {

    @BeforeEach
    void setUp() {
        member = buildMember();
        profileImage = buildProfileImage();
    }

    @Test
    void instance_reference_path_starts_with_file_storage() {
        assertThat(profileImage.getReferencePath()).startsWith("/file-storage/");
    }

    @Test
    void toEntity_before_saveLocal_throws_IllegalStateException() {
        assertThatThrownBy(() -> MemberProfileImageJpaEntity.of(profileImage))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void saveMemberProfileImage() {
        profileImage.saveLocal();
        Path output = Paths.get(profileImage.getFullPath());

        assertThat(output.toFile().exists()).isTrue();
    }

    @Test
    void toEntity_after_save() {
        // given
        profileImage.saveLocal();

        // when
        MemberProfileImageJpaEntity entity = MemberProfileImageJpaEntity.of(profileImage);

        // then
        assertThat(entity.getFullPath())
                .contains(MemberProfileImage.prepareDir(member.toModel().getId()), profileImage.getRenamedFilename());
    }

    @Test
    void remove_former_if_exists_then_save_new_one() {
        String targetDir = MemberProfileImage.prepareDir(member.toModel().getId());

        // given
        // dummies to be deleted.
        IntStream.range(0, 5).forEach(i -> {
            try {
                File file = TestImageCreator.createTestImage(100, 100, "sample image" + i);
                InputStream stream = Files.newInputStream(file.toPath());
                String fullPath = Paths.get(targetDir, file.getName()).toString();
                DefaultImageManager.saveLocal(stream, fullPath);
            } catch (IOException e) {
                log.info("IOException. occurred, check.");
                throw new RuntimeException(e.getClass().getName() + " " + e.getMessage());
            }
        });

        assertThat(Arrays.sizeOf(Paths.get(targetDir).toFile().listFiles()))
                .isGreaterThanOrEqualTo(5);

        // when
        // 이전 5 장의 더미 파일 삭제 및 테스트 필드 profileImage 객체 필드 로컬 저장.
        MemberProfileImage newImage = buildProfileImage("new one");
        newImage.saveLocal();

        assertThat(newImage.getSavedLocal()).isTrue();

        Path newImagePath = Paths.get(newImage.getFullPath());

        // then
        int targetDirFileCount = Arrays.sizeOf(newImagePath.getParent().toFile().listFiles());

        assertThat(newImagePath.toFile().exists()).isTrue();
        assertThat(targetDirFileCount).isEqualTo(1);
    }
}
