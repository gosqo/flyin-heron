package com.gosqo.flyinheron.domain;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 파일 취급 관련 변수명 컨벤션
 * <p>
 * OS 별 경로 구분자 불일치 문제에 대비, 경로 선택은 Paths.get(, , , ), 구분자 명시적 입력에는 File.separator 사용
 * <ul>
 * <li>-Filename: 단일 파일을 가리킬 때 사용. <b>확장자 포함 파일명</b>
 * <li>-Dir: 파일 시스템 폴더를 가리킬 때 사용.
 * <li>-FullPath: 디렉토리와 파일명을 연결한 경로.
 * </ul>
 */
@Slf4j
public class DefaultImageManager {
    public static final String LOCAL_STORAGE_DIR = System.getenv("FLYINHERON_STORAGE");

    public static String saveLocal(
            InputStream inputStream
            , String renamedFilename
            , String targetDir
    ) throws IOException {
        Path targetFolder = Paths.get(targetDir);

        if (!targetFolder.toFile().exists()) {
            Files.createDirectories(targetFolder);
        }

        Path output = Paths.get(targetDir, renamedFilename);
        Files.copy(inputStream, output);

        return output.toString();
    }

    public static String renameFileWithUuid(String originalFilename) {
        String spaceReplaced = originalFilename.replaceAll(" ", "-");

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String formattedDate = format.format(new Date());
        String uuid = UUID.randomUUID().toString();

        return formattedDate + "_" + uuid + "_" + spaceReplaced;
    }
}
