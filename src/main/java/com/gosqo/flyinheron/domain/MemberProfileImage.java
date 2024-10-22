package com.gosqo.flyinheron.domain;

import com.gosqo.flyinheron.repository.jpaentity.MemberProfileImageJpaEntity;
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

import static com.gosqo.flyinheron.domain.DefaultImageManager.LOCAL_STORAGE_DIR;

@Getter
@Slf4j
public class MemberProfileImage {
    static final Path MEMBER_PROFILE_IMAGE_DIR =
            Paths.get(DefaultImageManager.LOCAL_STORAGE_DIR, "member");

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
            Long memberId
            , InputStream inputStream
            , String originalFilename
            , String renamedFilename
            , String fullPath
    ) {
        Objects.requireNonNull(memberId);

        this.memberId = memberId;
        this.inputStream = inputStream;
        this.originalFilename = originalFilename;

        this.renamedFilename = this.renameFile(this.originalFilename);
        this.storageDir = Paths.get(
                MEMBER_PROFILE_IMAGE_DIR.toString()
                , this.memberId.toString()
                , "profile").toString();
    }

    public static MemberProfileImage of(MemberProfileImageJpaEntity entity) {
        return MemberProfileImage.builder()
                .memberId(entity.getMember().getId())
                .originalFilename(entity.getOriginalFilename())
                .renamedFilename(entity.getRenamedFilename())
                .fullPath(entity.getFullPath())
                .build();
    }

    public MemberProfileImageJpaEntity toEntity(Member member) {
        Objects.requireNonNull(this.fullPath, "image file full path can not be null or blank.");

        return MemberProfileImageJpaEntity.builder()
                .member(member) // null 일 경우 this 인스턴스를 필드로 가질 Member 타입 인스턴스에서 updateProfileImage() 사용
                .originalFilename(this.originalFilename)
                .renamedFilename(this.renamedFilename)
                .fullPath(this.fullPath)
                .build();
    }

    public String saveLocal() throws IOException {
        File targetFolder = Paths.get(this.storageDir).toFile();

        if (targetFolder.exists() && targetFolder.listFiles() != null) {
            deleteSubFiles(this.storageDir);
        }

        this.fullPath = DefaultImageManager.saveLocal(
                this.inputStream
                , this.renamedFilename
                , this.storageDir
        );

        return this.fullPath;
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

    private String renameFile(String originalFilename) {
        String spaceReplaced = originalFilename.replaceAll(" ", "-");

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String formattedDate = format.format(new Date());

        return formattedDate + "_" + spaceReplaced;
    }
}
