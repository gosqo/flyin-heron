package com.gosqo.flyinheron.domain;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Slf4j
public abstract class AbstractImageManager {
    protected static final String LOCAL_STORAGE_PATH = System.getenv("FLYINHERON_STORAGE");

    protected String saveLocal(
            InputStream inputStream
            , String replacedFilename
            , String targetDir
            , CopyOption copyOption
    ) throws IOException {
        Path target = Paths.get(targetDir);

        if (!target.toFile().exists()) {
            Files.createDirectories(target);
        }

        Path fullPath = Paths.get(targetDir, replacedFilename);

        if (copyOption == null) {
            Files.copy(inputStream, fullPath);
        } else {
            Files.copy(inputStream, fullPath, copyOption);
        }

        return fullPath.toString();
    }

    protected String renameFile(String originalFilename) {
        String spaceReplaced = originalFilename.replaceAll(" ", "-");

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String uuid = UUID.randomUUID().toString();
        String formattedDate = format.format(new Date());

        return formattedDate + "_" + uuid + "_" + spaceReplaced;
    }
}
