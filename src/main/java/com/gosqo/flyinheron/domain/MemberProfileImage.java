package com.gosqo.flyinheron.domain;

import lombok.Builder;
import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;

import static com.gosqo.flyinheron.domain.MemberProfileImageManager.MEMBER_PROFILE_IMAGE_PATH;

@Getter
public class MemberProfileImage {
    private static final CopyOption MEMBER_PROFILE_IMAGE_COPYOPTION = StandardCopyOption.REPLACE_EXISTING;

    private final MemberProfileImageManager manager;
    private final Long memberId;
    private final InputStream inputStream;
    private final String originalFilename;
    private final String renamedFilename;
    private final String targetDir;
    private final Boolean exist;
    private String fileFullPath;

    @Builder
    public MemberProfileImage(
            MemberProfileImageManager manager
            , Long memberId
            , InputStream inputStream
            , String originalFilename
            , String fileFullPath
    ) {
        this.manager = manager;
        this.memberId = memberId;
        this.inputStream = inputStream;
        this.originalFilename = originalFilename;
        this.fileFullPath = fileFullPath;

        this.exist = this.fileFullPath != null;
        this.renamedFilename = manager.renameFile(this.originalFilename);
        this.targetDir = MEMBER_PROFILE_IMAGE_PATH + this.memberId.toString() + "/profile/";
    }

    public static MemberProfileImage of(MemberProfileImageJpaEntity entity) {
        return MemberProfileImage.builder()
                // ...
                .fileFullPath(entity.getFileFullPath())
                .build();
    }

    public String saveMemberProfileImage() throws IOException {

        if (this.exist) {
            deleteSubFilesOf();
        }

        this.fileFullPath = manager.saveLocal(
                this.inputStream, this.renamedFilename, this.targetDir, MEMBER_PROFILE_IMAGE_COPYOPTION
        );

        return this.fileFullPath;
    }

    private void deleteSubFilesOf() throws IOException {
        String target = this.fileFullPath.substring(0, this.fileFullPath.lastIndexOf("/"));
        Path deletePathRoot = Paths.get(target);
        Files.walk(deletePathRoot)
                .filter(path -> !path.equals(deletePathRoot))
                .map(Path::toFile).filter(file -> !file.delete()).map(file -> "Failed to delete: " + file).forEach(System.err::println);
    }

    public MemberProfileImageJpaEntity toEntity() {
        if (fileFullPath == null || fileFullPath.isBlank()) {
            throw new IllegalStateException("image file full path can not be null or blank.");
        }

        return MemberProfileImageJpaEntity.builder()
                .memberId(this.memberId)
                .originalFilename(this.originalFilename)
                .fileFullPath(this.fileFullPath)
                .build();
    }
}
