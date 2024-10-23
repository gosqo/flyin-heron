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
        @PersistenceContext
        private EntityManager em;

        @Autowired
        Repository_Only(
                MemberRepository memberRepository
                , MemberProfileImageRepository memberProfileImageRepository
        ) {
            this.memberRepository = memberRepository;
            this.memberProfileImageRepository = memberProfileImageRepository;
        }

        @BeforeEach
        void setUp() throws IOException {
            member = memberRepository.save(buildMember());
            profileImageJpaEntity = memberProfileImageRepository.save(buildProfileImageJpaEntity());

            em.flush();
            em.clear();
        }

        @Test
        void saved_profile_image_entity_can_refer_its_member() {
            // when
            MemberProfileImageJpaEntity found =
                    memberProfileImageRepository.findById(profileImageJpaEntity.getId()).orElseThrow();

            //then
            assertThat(found.getMember().getId()).isEqualTo(member.getId());
        }

        @Test
        void saved_profile_image_entity_can_be_referenced_by_its_member() {
            // when
            Member found = memberRepository.findById(member.getId()).orElseThrow();

            //then
            assertThat(found.getProfileImage().getId()).isEqualTo(profileImageJpaEntity.getId());
        }
    }

    @Nested
    @Import(MemberProfileImageService.class)
    class With_Service extends RepositoryTestBase {
        private final MemberProfileImageService service;
        private final MemberRepository memberRepository;
        private final MemberProfileImageRepository memberProfileImageRepository;
        @PersistenceContext
        private EntityManager em;

        @Autowired
        With_Service(
                MemberProfileImageService memberProfileImageService
                , MemberRepository memberRepository
                , MemberProfileImageRepository memberProfileImageRepository
        ) {
            this.service = memberProfileImageService;
            this.memberRepository = memberRepository;
            this.memberProfileImageRepository = memberProfileImageRepository;
        }

        @BeforeEach
        void setUp() throws IOException {
            member = memberRepository.save(buildMember());
            profileImageJpaEntity = memberProfileImageRepository.save(buildProfileImageJpaEntity());

            em.flush();
            em.clear();
        }

        @Test
        void saved_profile_image_can_referenced_by_its_member() {
            Member found = memberRepository.findById(member.getId()).orElseThrow();

            assertThat(found.getProfileImage().getId()).isEqualTo(profileImageJpaEntity.getId());
        }

        @Test
        void remove_profile_image_it_actually_turns_it_into_default_image() throws IOException {
            service.removeMemberProfileImage(member.getEmail());

            Member found = memberRepository.findByEmail(member.getEmail()).orElseThrow();

            assertThat(found.getProfileImage()).isNotNull();
            assertThat(found.getProfileImage().getFullPath()).contains(member.getNickname());
        }
    }
}
