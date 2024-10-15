package com.gosqo.flyinheron.domain;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.gosqo.flyinheron.domain.AbstractImageManager.LOCAL_STORAGE_PATH;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class AbstractImageManagerTest {
    static final String IMAGE_CLIENT_PATH = LOCAL_STORAGE_PATH + "/client/";
    static final String CLIENT_IMAGE_FILENAME = "test.png";

    private final AbstractImageManager manager = new AbstractImageManager() {};

    @Test
    void saveLocal() throws IOException {
        String originalFileName = "hello World!.png";
        String targetDir = LOCAL_STORAGE_PATH + "test/common/";
        String renamedFilename = manager.renameFile(originalFileName);
        String fullPath = "";
        Path source = Paths.get(IMAGE_CLIENT_PATH + CLIENT_IMAGE_FILENAME);

        try (InputStream inputStream = Files.newInputStream(source)) {
            fullPath = manager.saveLocal(inputStream, renamedFilename, targetDir, null);
        }

        assertThat(fullPath).isNotBlank();
    }
}