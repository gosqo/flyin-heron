package com.gosqo.flyinheron.domain;

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
import java.util.Objects;
import java.util.stream.Stream;

@Getter
@Slf4j
public class MemberProfileImage {
    static final Path MEMBER_PROFILE_IMAGE_DIR =
            Paths.get(DefaultImageManager.LOCAL_STORAGE_DIR, "member");

    private final Member member;

    // domain 의 프레임워크 의존성(스프링 MultipartFile) 탈피를 위해 아래 inputStream, originalFilename 을 따로 받음.
    // 생성자, 빌더를 통한 this 인스턴스 생성 시, 인자는 동일한 MultipartFile 객체 메서드(getInputStream, getOriginalFilename)를 통해
    // this 인스턴스 각각의 필드를 채웁니다.
    private final InputStream inputStream;
    private final String originalFilename;
    private final String storageDir;
    private final String renamedFilename;
    private final String fullPath;
    private final String referencePath;

    private boolean savedLocal = false;

    @Builder
    public MemberProfileImage(
            Member member
            , InputStream inputStream
            , String originalFilename
            , String referencePath
            , String renamedFilename
            , String fullPath
            , boolean savedLocal
    ) {
        Objects.requireNonNull(member);

        this.member = member;
        this.inputStream = inputStream;
        this.originalFilename = originalFilename;

        this.storageDir = prepareDir(this.member);
        this.renamedFilename = this.renameFile(this.originalFilename);
        this.fullPath = Paths.get(this.storageDir, this.renamedFilename).toString();
        this.referencePath = this.fullPath.replaceAll(DefaultImageManager.LOCAL_STORAGE_DIR, "");
    }

    public static String prepareDir(Member member) {
        return Paths.get(
                MEMBER_PROFILE_IMAGE_DIR.toString()
                , String.valueOf(member.getId())
                , "profile"
        ).toString();
    }

    public static MemberProfileImage createDefaultImage(Member member) throws IOException {
        File defaultProfileImage =
                DefaultImageManager.createDefaultMemberProfileImage(100, 100, member.getNickname());

        return MemberProfileImage.builder()
                .member(member)
                .inputStream(Files.newInputStream(defaultProfileImage.toPath()))
                .originalFilename(defaultProfileImage.getName())
                .build();
    }

    public void saveLocal() throws IOException {
        File targetFolder = Paths.get(this.storageDir).toFile();

        if (targetFolder.exists() && targetFolder.listFiles() != null) {
            deleteSubFiles(this.storageDir);
        }

        DefaultImageManager.saveLocal(this.inputStream, this.fullPath);
        this.savedLocal = true;
    }

    private void deleteSubFiles(String targetDir) throws IOException {

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
        }
    }

    private String renameFile(String originalFilename) {
        String spaceReplaced = originalFilename.replaceAll(" ", "-");

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String formattedDate = format.format(new Date());

        return formattedDate + "_" + spaceReplaced;
    }
}
