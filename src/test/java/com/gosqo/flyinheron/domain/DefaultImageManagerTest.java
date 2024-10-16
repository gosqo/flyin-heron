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
    static final String CLIENT_IMAGE_DIR = LOCAL_STORAGE_DIR + "/client/";
    static final String CLIENT_IMAGE_FILENAME = "test.png";
    static final String TEST_FILE_FULL_PATH = CLIENT_IMAGE_DIR + CLIENT_IMAGE_FILENAME;

    private final DefaultImageManager manager;

    DefaultImageManagerTest() {
        manager = new DefaultImageManager();
    }

    @Test
    void saveLocal() throws IOException {
        // given
        String fullPath = ""; // need to be filled to assert test result.
        String targetDir = LOCAL_STORAGE_DIR + "test/common/";
        String renamedFilename = manager.renameFile(CLIENT_IMAGE_FILENAME);
        Path source = Paths.get(TEST_FILE_FULL_PATH);

        // when
        try (InputStream inputStream = Files.newInputStream(source)) {
            fullPath = manager.saveLocal(inputStream, renamedFilename, targetDir);
        }

        // then
        assertThat(fullPath).isNotBlank();

        Path output = Paths.get(fullPath);

        assertThat(output.toFile().exists()).isTrue();
    }
}
