package com.gosqo.flyinheron.domain;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;

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
    public static final String LOCAL_STORAGE_DIR;

    static {
        String temp = System.getenv("FLYINHERON_STORAGE");

        if (temp == null) {
            throw new IllegalStateException("LOCAL_STORAGE_DIR not set.");
        }

        if (File.separator.equals("\\")) {
            LOCAL_STORAGE_DIR = temp.replaceAll("/", Matcher.quoteReplacement(File.separator));
        } else {
            LOCAL_STORAGE_DIR = temp;
        }
    }

    public static final String WEB_FILE_PATH = "/file-storage";

    public static void saveLocal(InputStream inputStream, String fullPath) {
        Path targetFullPath = Paths.get(fullPath);
        Path targetFolder = targetFullPath.getParent();

        try (inputStream) {

            if (!targetFolder.toFile().exists()) {
                Files.createDirectories(targetFolder);
            }

            Files.copy(inputStream, targetFullPath);
        } catch (IOException e) {
            log.error("I/O Error", e);
            throw new RuntimeException(e.getClass().getName() + " " + e.getMessage());
        }
    }

    public static File createDefaultMemberProfileImage(int width, int height, String username) {
        String firstCharacter = String.valueOf(username.charAt(0));
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        File tempFile;

        g2d.setColor(Color.ORANGE);
        g2d.fillRect(width / 2, height / 2, width, height);
        g2d.fillRect(0, 0, width / 2, height / 2);

        g2d.setColor(new Color(255, 255, 255));
        g2d.drawString(firstCharacter, width / 100 * 48, height / 100 * 53);

        g2d.dispose();

        try {
            tempFile = File.createTempFile(username + "_", ".png");
            ImageIO.write(bufferedImage, "png", tempFile);
        } catch (IOException e) {
            log.error("I/O Error", e);
            throw new RuntimeException(e.getClass().getName() + " " + e.getMessage());
        }
        return tempFile;
    }

    public static String renameFileWithUuid(String originalFilename) {
        String spaceReplaced = originalFilename.replaceAll(" ", "-");

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String formattedDate = format.format(new Date());
        String uuid = UUID.randomUUID().toString();

        return formattedDate + "_" + uuid + "_" + spaceReplaced;
    }
}
