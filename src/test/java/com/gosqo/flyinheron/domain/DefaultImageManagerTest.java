package com.gosqo.flyinheron.domain;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.gosqo.flyinheron.domain.DefaultImageManager.LOCAL_STORAGE_DIR;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class DefaultImageManagerTest {
    static final String CLIENT_IMAGE_FILENAME = "test.png";
    static final Path CLIENT_IMAGE_DIR = Paths.get(LOCAL_STORAGE_DIR, "client");
    static final Path TEST_FILE_FULL_PATH = Paths.get(CLIENT_IMAGE_DIR.toString(), CLIENT_IMAGE_FILENAME);

    @Test
    void saveLocal() throws IOException {
        // given
        String fullPath = ""; // need to be filled to assert test result.
        String targetDir = LOCAL_STORAGE_DIR + "test/common/";
        String renamedFilename = DefaultImageManager.renameFileWithUuid(CLIENT_IMAGE_FILENAME);

        // when
        try (InputStream inputStream = Files.newInputStream(TEST_FILE_FULL_PATH)) {
            fullPath = DefaultImageManager.saveLocal(inputStream, renamedFilename, targetDir);
        }

        // then
        assertThat(fullPath).isNotBlank();

        Path output = Paths.get(fullPath);

        assertThat(output.toFile().exists()).isTrue();
    }
}
