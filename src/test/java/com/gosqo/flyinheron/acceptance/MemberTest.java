package com.gosqo.flyinheron.acceptance;

import com.gosqo.flyinheron.domain.Member;
import com.gosqo.flyinheron.domain.fixture.MemberFixture;
import com.gosqo.flyinheron.dto.member.MemberRegisterRequest;
import com.gosqo.flyinheron.global.data.TestDataRemover;
import com.gosqo.flyinheron.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.RequestEntity;

import static org.assertj.core.api.Assertions.assertThat;

public class MemberTest extends SpringBootTestBase {
    private final MemberRepository memberRepository;

    @Autowired
    public MemberTest(
            TestRestTemplate template,
            TestDataRemover remover, MemberRepository memberRepository
    ) {
        super(template, remover);
        this.memberRepository = memberRepository;
    }

    @Test
    void when_register_member_profile_image_being_filled() {
        MemberRegisterRequest requestBody =
                new MemberRegisterRequest(
                        MemberFixture.EMAIL
                        , MemberFixture.PASSWORD
                        , MemberFixture.PASSWORD
                        , MemberFixture.NICKNAME
                );
        RequestEntity<MemberRegisterRequest> request = RequestEntity
                .post("/api/v1/member")
                .body(requestBody);

        template.exchange(request, String.class);

        Member found = memberRepository.findByEmail(MemberFixture.EMAIL).orElseThrow();

        assertThat(found.getProfileImage()).isNotNull();
    }
}
