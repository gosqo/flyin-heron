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
 * String 객체:
 * <ul>
 * <li>-Filename: 단일 파일을 가리킬 때 사용. <b>확장자 포함 파일명</b>
 * <li>-Dir: 시스템 구조상 폴더를 가리킬 때 사용. <b>해당 형식의 변수명을 가진 경우, 할당값의 마지막에 "/"을 포함한다.</b>
 * <li>-FullPath: 디렉토리와 파일명을 연결한 경로.
 * </ul>
 *
 * <p>
 * Path, File 객체:
 * <ul>
 * <li>-Source, -Output, saved, or stored ...: 파일을 잡은 경우. 보통 fullPath 를 사용해 잡음.
 * <li>-Folder: Path 객체로 폴더를 잡은 경우에 사용.
 * </ul>
 */
@Slf4j
public class DefaultImageManager {
    public static final String LOCAL_STORAGE_DIR = System.getenv("FLYINHERON_STORAGE");

    protected String saveLocal(
            InputStream inputStream
            , String replacedFilename
            , String targetDir
    ) throws IOException {
        Path targetFolder = Paths.get(targetDir);

        if (!targetFolder.toFile().exists()) {
            Files.createDirectories(targetFolder);
        }

        Path output = Paths.get(targetDir, replacedFilename);
        Files.copy(inputStream, output);

        return output.toString();
    }

    protected String renameFile(String originalFilename) {
        String spaceReplaced = originalFilename.replaceAll(" ", "-");

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String formattedDate = format.format(new Date());
        String uuid = UUID.randomUUID().toString();

        return formattedDate + "_" + uuid + "_" + spaceReplaced;
    }
}
