package com.gosqo.flyinheron.repository;

import com.gosqo.flyinheron.domain.Member;
import com.gosqo.flyinheron.repository.jpaentity.MemberProfileImageJpaEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class MemberProfileImageRepositoryTest extends RepositoryTestBase {
    private final MemberRepository memberRepository;
    private final MemberProfileImageRepository memberProfileImageRepository;
    @PersistenceContext
    private EntityManager em;

    @Autowired
    MemberProfileImageRepositoryTest(
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
