package com.gosqo.flyinheron.domain;

import com.gosqo.flyinheron.dto.memberprofileimage.MemberProfileImageLightInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.stream.Stream;

@Getter
@Slf4j
public class MemberProfileImage {
    static final Path MEMBER_PROFILE_IMAGE_DIR =
            Paths.get(DefaultImageManager.LOCAL_STORAGE_DIR, "member");

    private final InputStream inputStream;
    private final String originalFilename;
    private final String storageDir;
    private final String renamedFilename;
    private final String fullPath;
    private final String referencePath;

    private final Long memberId;
    private MemberModel member;

    private Boolean savedLocal = Boolean.FALSE;

    @Builder
    public MemberProfileImage(
            MemberModel member
            , Long memberId
            , InputStream inputStream
            , String originalFilename
            , String referencePath
            , String renamedFilename
            , String fullPath
            , Boolean savedLocal
    ) {
        this.member = member;
        this.memberId = memberId == null ? member.getId() : memberId;
        this.inputStream = inputStream;
        this.originalFilename = originalFilename;

        this.storageDir = prepareDir(this.memberId);

        this.renamedFilename = renamedFilename == null
                ? this.renameFile(this.originalFilename)
                : renamedFilename;

        this.fullPath = fullPath == null
                ? Paths.get(this.storageDir, this.renamedFilename).toString()
                : fullPath;

        this.referencePath = referencePath == null
                ? this.fullPath
                .replaceAll(
                        Matcher.quoteReplacement(DefaultImageManager.LOCAL_STORAGE_DIR)
                        , DefaultImageManager.WEB_FILE_PATH
                )
                .replaceAll(Matcher.quoteReplacement(File.separator), "/")
                : referencePath;

        this.savedLocal = savedLocal != null && savedLocal;
    }

    public static String prepareDir(Long memberId) {
        return Paths.get(
                MEMBER_PROFILE_IMAGE_DIR.toString()
                , String.valueOf(memberId)
                , "profile"
        ).toString();
    }

    public static MemberProfileImage convertToProfileImage(MemberModel member, File file) {
        MemberProfileImage image;

        try {
            image = MemberProfileImage.builder()
                    .member(member)
                    .inputStream(Files.newInputStream(file.toPath()))
                    .originalFilename(file.getName())
                    .build();
        } catch (IOException e) {
            log.error("I/O Error", e);
            throw new RuntimeException(e.getClass().getName() + " " + e.getMessage());
        }

        return image;
    }

    public static MemberProfileImage createDefaultImage(MemberModel member) {
        File defaultProfileImage =
                DefaultImageManager.createDefaultMemberProfileImage(100, 100, member.getNickname());

        return MemberProfileImage.convertToProfileImage(member, defaultProfileImage);
    }

    public void saveLocal() {
        File targetFolder = Paths.get(this.storageDir).toFile();

        if (targetFolder.exists() && targetFolder.listFiles() != null) {
            deleteSubFiles(this.storageDir);
        }

        DefaultImageManager.saveLocal(this.inputStream, this.fullPath);
        this.savedLocal = true;
    }

    public MemberProfileImageLightInfo toLightInfo() {
        return MemberProfileImageLightInfo.builder()
                .referencePath(this.referencePath)
                .originalFilename(this.originalFilename)
                .build();
    }

    private void deleteSubFiles(String targetDir) {

        if (!targetDir.startsWith(DefaultImageManager.LOCAL_STORAGE_DIR)
                || targetDir.equals(DefaultImageManager.LOCAL_STORAGE_DIR)
        ) {
            throw new IllegalArgumentException("argument targetDir should refer under the LOCAL_STORAGE_PATH");
        }

        Path targetFolder = Paths.get(targetDir);

        try (Stream<Path> files = Files.walk(targetFolder)) {
            files
                    .filter(path -> !path.equals(targetFolder))
                    .map(Path::toFile)
                    .filter(file -> !file.delete())
                    .map(file -> "Failed to delete: " + file)
                    .forEach(log::warn);
        } catch (IOException e) {
            log.error("I/O Error", e);
            throw new RuntimeException(e.getClass().getName() + " " + e.getMessage());
        }
    }

    private String renameFile(String originalFilename) {
        String spaceReplaced = originalFilename.replaceAll(" ", "-");

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String formattedDate = format.format(new Date());

        return formattedDate + "_" + spaceReplaced;
    }
}
