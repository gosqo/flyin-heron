package com.gosqo.flyinheron.domain;

import com.gosqo.flyinheron.repository.jpaentity.MemberProfileImageJpaEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Objects;
import java.util.stream.Stream;

import static com.gosqo.flyinheron.domain.DefaultImageManager.LOCAL_STORAGE_DIR;
import static com.gosqo.flyinheron.domain.MemberProfileImageManager.MEMBER_IMAGE_DIR;

@Getter
@Slf4j
public class MemberProfileImage {
    private static final CopyOption MEMBER_PROFILE_IMAGE_COPYOPTION = StandardCopyOption.REPLACE_EXISTING;

    private final MemberProfileImageManager manager;
    private final Long memberId;

    // domain 의 프레임워크 의존성(스프링 MultipartFile) 탈피를 위해 아래 inputStream, originalFilename 을 따로 받음.
    // 생성자, 빌더를 통한 this 인스턴스 생성 시, 인자는 동일한 MultipartFile 객체 메서드(getInputStream, getOriginalFilename)를 통해
    // this 인스턴스 각각의 필드를 채웁니다.
    private final InputStream inputStream;
    private final String originalFilename;
    private final String renamedFilename;
    private final String storageDir;
    private String fullPath;

    @Builder
    public MemberProfileImage(
            MemberProfileImageManager manager
            , Long memberId
            , InputStream inputStream
            , String originalFilename
            , String renamedFilename
            , String fullPath
    ) {
        Objects.requireNonNull(memberId);

        this.manager = manager;
        this.memberId = memberId;
        this.inputStream = inputStream;
        this.originalFilename = originalFilename;

        this.renamedFilename = manager.renameFile(this.originalFilename);
        this.storageDir = MEMBER_IMAGE_DIR + this.memberId + "/profile/";
    }

    public static MemberProfileImage of(MemberProfileImageJpaEntity entity) {
        return MemberProfileImage.builder()
                .memberId(entity.getMember().getId())
                .originalFilename(entity.getOriginalFilename())
                .renamedFilename(entity.getRenamedFilename())
                .fullPath(entity.getFullPath())
                .build();
    }

    public String saveLocal() throws IOException {
        File targetFolder = Paths.get(this.storageDir).toFile();

        if (targetFolder.exists() && targetFolder.listFiles() != null) {
            deleteSubFiles(this.storageDir);
        }

        this.fullPath = manager.saveLocal(this.inputStream, this.renamedFilename, this.storageDir);

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

    public MemberProfileImageJpaEntity toEntity(Member member) {

        if (this.fullPath == null || this.fullPath.isBlank()) {
            throw new IllegalStateException("image file full path can not be null or blank.");
        }

        return MemberProfileImageJpaEntity.builder()
                .member(member)
                .originalFilename(this.originalFilename)
                .renamedFilename(this.renamedFilename)
                .fullPath(this.fullPath)
                .build();
    }
}
