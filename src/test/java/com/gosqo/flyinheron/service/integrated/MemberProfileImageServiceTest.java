package com.gosqo.flyinheron.service.integrated;

import com.gosqo.flyinheron.domain.Member;
import com.gosqo.flyinheron.global.data.TestDataRemover;
import com.gosqo.flyinheron.global.data.TestImageCreator;
import com.gosqo.flyinheron.repository.MemberProfileImageRepository;
import com.gosqo.flyinheron.repository.MemberRepository;
import com.gosqo.flyinheron.service.MemberProfileImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

public class MemberProfileImageServiceTest extends IntegratedServiceTestBase {
    private final MemberProfileImageService service;
    private final MemberRepository memberRepository;
    private final MemberProfileImageRepository memberProfileImageRepository;

    @Autowired
    MemberProfileImageServiceTest(
            TestDataRemover remover
            , MemberProfileImageService memberProfileImageService
            , MemberRepository memberRepository
            , MemberProfileImageRepository memberProfileImageRepository
    ) {
        super(remover);
        this.service = memberProfileImageService;
        this.memberRepository = memberRepository;
        this.memberProfileImageRepository = memberProfileImageRepository;
    }

    @BeforeEach
    void setUp() {
        member = memberRepository.save(buildMember());
        profileImageJpaEntity = memberProfileImageRepository.save(buildProfileImageJpaEntity());
    }

    @Test
    void saved_profile_image_can_referenced_by_its_member() {
        Member found = memberRepository.findById(member.getId()).orElseThrow();

        assertThat(found.getProfileImage().getId()).isEqualTo(profileImageJpaEntity.getId());
    }

    @Test
    void remove_profile_image_it_actually_turns_it_into_default_image() {
        service.removeMemberProfileImage(member.getEmail(), member.getId());

        Member found = memberRepository.findByEmail(member.getEmail()).orElseThrow();

        assertThat(found.getProfileImage()).isNotNull();
        assertThat(found.getProfileImage().getFullPath()).contains(member.getNickname());
    }

    @Test
    void update_profile_image() throws IOException {
        File image = TestImageCreator.createTestImage(100, 100, "test image");
        MultipartFile file = new MockMultipartFile(
                image.getName()
                , image.getName()
                , "image/png"
                , Files.newInputStream(image.toPath())
        );

        service.updateMemberProfileImage(file, member.getEmail(), member.getId());

        Member found = memberRepository.findByEmail(member.getEmail()).orElseThrow();

        assertThat(found.getProfileImage()).isNotNull();
        assertThat(found.getProfileImage().getFullPath()).contains(image.getName().replaceAll(" ", "-"));
    }
}
