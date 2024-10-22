package com.gosqo.flyinheron.repository;

import com.gosqo.flyinheron.domain.Member;
import com.gosqo.flyinheron.domain.MemberProfileImage;
import com.gosqo.flyinheron.global.data.TestImageCreator;
import com.gosqo.flyinheron.repository.jpaentity.MemberProfileImageJpaEntity;
import com.gosqo.flyinheron.service.MemberProfileImageService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class MemberProfileImageRepositoryTest {

    @Nested
    class Repository_Only extends RepositoryTestBase {
        private final MemberRepository memberRepository;
        private final MemberProfileImageRepository memberProfileImageRepository;
        private File sampleImage;
        @PersistenceContext
        private EntityManager em;

        @Autowired
        Repository_Only(
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

            em.flush();
            em.clear();
        }

        @Test
        void save() throws IOException {
            // given
            MemberProfileImage image = MemberProfileImage.builder()
                    .member(member)
                    .inputStream(Files.newInputStream(sampleImage.toPath()))
                    .originalFilename("hello image.png")
                    .build();
            image.saveLocal();

            MemberProfileImageJpaEntity entity = image.toEntity();
            MemberProfileImageJpaEntity stored = memberProfileImageRepository.save(entity);
            em.flush();
            em.clear();

            // when
            MemberProfileImageJpaEntity found = memberProfileImageRepository.findById(stored.getId()).orElseThrow();

            //then
            assertThat(found.getMember().getId()).isEqualTo(member.getId());
        }
    }

    @Nested
    @Import(MemberProfileImageService.class)
    class With_Service extends RepositoryTestBase {
        private final MemberProfileImageService service;
        private final MemberRepository memberRepository;
        private final MemberProfileImageRepository memberProfileImageRepository;
        private MemberProfileImageJpaEntity entity;
        private File sampleImage;
        @PersistenceContext
        private EntityManager em;

        @Autowired
        With_Service(
                MemberProfileImageService memberProfileImageService,
                MemberRepository memberRepository,
                MemberProfileImageRepository memberProfileImageRepository
        ) {
            this.service = memberProfileImageService;
            this.memberRepository = memberRepository;
            this.memberProfileImageRepository = memberProfileImageRepository;
        }

        @BeforeEach
        void setUp() throws IOException {
            member = memberRepository.save(buildMember());
            sampleImage = TestImageCreator.createTestImage(100, 100, "Test image 0");

            MemberProfileImage image = MemberProfileImage.builder()
                    .member(member)
                    .inputStream(Files.newInputStream(sampleImage.toPath()))
                    .originalFilename("hello image.png")
                    .build();
            image.saveLocal();

            MemberProfileImageJpaEntity entity1 = image.toEntity();
            entity = memberProfileImageRepository.save(entity1);
            member.updateProfileImage(entity1);

            em.flush();
            em.clear();
        }

        @Test
        void remove_profile_image() throws IOException {
            service.removeMemberProfileImage(member.getEmail());

            Member found = memberRepository.findByEmail(member.getEmail()).orElseThrow();

            assertThat(found.getProfileImage()).isNotNull();
            assertThat(found.getProfileImage().getFullPath()).contains(member.getNickname());
        }
    }
}
