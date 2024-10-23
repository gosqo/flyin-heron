package com.gosqo.flyinheron.repository.jpaentity;

import com.gosqo.flyinheron.domain.Member;
import com.gosqo.flyinheron.global.jpadirect.JpaDirectTestDataManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.gosqo.flyinheron.domain.DefaultImageManager.LOCAL_STORAGE_DIR;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class MemberProfileImageJpaEntityTest extends JpaDirectTestDataManager {
    private static final String CLIENT_IMAGE_FILENAME = "profile image.png";
    private static final Path CLIENT_IMAGE_DIR = Paths.get(LOCAL_STORAGE_DIR, "client");
    private static final Path SOURCE = Paths.get(CLIENT_IMAGE_DIR.toString(), CLIENT_IMAGE_FILENAME);

    @BeforeEach
    void setUp() {
        initMember();
        log.info("==== Test data initialized. ====");
    }

    @Test
    void persist() throws IOException {
        // given
        Member member = em.find(Member.class, super.member.getId());
        InputStream in = Files.newInputStream(SOURCE);

        profileImage = buildProfileImage();

        // 로컬에 이미지 저장, 해당 객체 fullPath 할당
        // toEntity() 호출 이전에 저장하지 않으면 fullPath == null 로 예외 던짐.
        profileImage.saveLocal();

        MemberProfileImageJpaEntity entity = profileImage.toEntity(); // 아래 두 줄과 교체 시, 결과 같음

//        MemberProfileImageJpaEntity entity = image.toEntity(null);
//        member.updateProfileImage(entity);

        em.persist(entity);
        em.flush(); // 데이터베이스로 지연 쿼리 발송.
        em.clear(); // 데이터베이스에서 조회하기 위해 현재 영속성 컨텍스트 비움.

        assertThat(em.contains(entity)).isFalse();
        assertThat(em.contains(member)).isFalse();

        // when

        // Member.profileImage @OneToOne FetchType.EAGER 로, 멤버 조회하며 이미지 엔티티도 (join)조회함.
        // LAZY 로 하더라도 쿼리가 분리될 뿐, 이미지도 조회함.
        Member foundMember = em.find(Member.class, member.getId());

        // 아래는 이미 영속성 컨텍스트에 존재하기 때문에 영속성 컨텍스트에서 그대로 가져옴.
        MemberProfileImageJpaEntity foundProfileImage = em.find(MemberProfileImageJpaEntity.class, entity.getId());

        // then
        assertThat(foundMember.getProfileImage().getFullPath()).isEqualTo(foundProfileImage.getFullPath());
    }
}
