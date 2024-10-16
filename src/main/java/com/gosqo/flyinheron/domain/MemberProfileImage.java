package com.gosqo.flyinheron.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.stream.Stream;

import static com.gosqo.flyinheron.domain.DefaultImageManager.LOCAL_STORAGE_DIR;
import static com.gosqo.flyinheron.domain.MemberProfileImageManager.MEMBER_IMAGE_DIR;

@Getter
@Slf4j
public class MemberProfileImage {
    private static final CopyOption MEMBER_PROFILE_IMAGE_COPYOPTION = StandardCopyOption.REPLACE_EXISTING;

    private final MemberProfileImageManager manager;
    private final Long memberId;
    private final InputStream inputStream;
    private final String originalFilename;
    private final String renamedFilename;
    private final String targetDir;
    private String fullPath;

    @Builder
    public MemberProfileImage(
            MemberProfileImageManager manager
            , Long memberId
            , InputStream inputStream
            , String originalFilename
            , String fullPath
    ) {
        this.manager = manager;
        this.memberId = memberId;
        this.inputStream = inputStream;
        this.originalFilename = originalFilename;
        this.fullPath = fullPath;

        this.renamedFilename = manager.renameFile(this.originalFilename);
        this.targetDir = MEMBER_IMAGE_DIR + this.memberId + "/profile/";
    }

    public static MemberProfileImage of(MemberProfileImageJpaEntity entity) {
        return MemberProfileImage.builder()
                // ...
                .fullPath(entity.getFullPath())
                .build();
    }

    public String saveMemberProfileImage() throws IOException {
        File targetFolder = Paths.get(this.targetDir).toFile();

        if (targetFolder.exists() && targetFolder.listFiles() != null) {
            deleteSubFiles(this.targetDir);
        }

        this.fullPath = manager.saveLocal(this.inputStream, this.renamedFilename, this.targetDir);

        return this.fullPath;
    }

    private void deleteSubFiles(String targetDir) throws IOException {

        if (!targetDir.startsWith(LOCAL_STORAGE_DIR) || targetDir.equals(LOCAL_STORAGE_DIR)) {
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

    public MemberProfileImageJpaEntity toEntity() {

        if (this.fullPath == null || this.fullPath.isBlank()) {
            throw new IllegalStateException("image file full path can not be null or blank.");
        }

        return MemberProfileImageJpaEntity.builder()
                .memberId(this.memberId)
                .originalFilename(this.originalFilename)
                .fullPath(this.fullPath)
                .build();
    }
}
