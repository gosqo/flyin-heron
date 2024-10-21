package com.gosqo.flyinheron.repository;

import com.gosqo.flyinheron.domain.MemberProfileImage;
import com.gosqo.flyinheron.domain.MemberProfileImageManager;
import com.gosqo.flyinheron.global.data.TestImageCreator;
import com.gosqo.flyinheron.repository.jpaentity.MemberProfileImageJpaEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class MemberProfileImageRepositoryTest extends RepositoryTestBase {

    private final MemberRepository memberRepository;
    private final MemberProfileImageRepository memberProfileImageRepository;
    private File sampleImage;
    @PersistenceContext
    private EntityManager em;

    @Autowired
    MemberProfileImageRepositoryTest(
            MemberRepository memberRepository,
            MemberProfileImageRepository memberProfileImageRepository
    ) {
        this.memberRepository = memberRepository;
        this.memberProfileImageRepository = memberProfileImageRepository;
    }

    @BeforeEach
    void setUp() throws IOException {
        member = memberRepository.save(buildMember());
        sampleImage = TestImageCreator.createTestImage(100, 100, "Test image 0");
    }

    @Test
    void save() throws IOException {
        // given
        MemberProfileImage image = MemberProfileImage.builder()
                .memberId(member.getId())
                .manager(new MemberProfileImageManager())
                .inputStream(Files.newInputStream(sampleImage.toPath()))
                .originalFilename("hello image.png")
                .build();
        image.saveLocal();

        MemberProfileImageJpaEntity entity = image.toEntity(member);
        MemberProfileImageJpaEntity stored = memberProfileImageRepository.save(entity);
        em.flush();
        em.clear();

        // when
        MemberProfileImageJpaEntity found = memberProfileImageRepository.findById(stored.getId()).orElseThrow();

        //then
        assertThat(found.getMember().getId()).isEqualTo(member.getId());
    }
}
