package com.gosqo.flyinheron.acceptance;

import com.gosqo.flyinheron.controller.AuthCookieManager;
import com.gosqo.flyinheron.domain.Member;
import com.gosqo.flyinheron.domain.fixture.MemberFixture;
import com.gosqo.flyinheron.dto.member.MemberRegisterRequest;
import com.gosqo.flyinheron.dto.member.MemberRegisterResponse;
import com.gosqo.flyinheron.global.data.TestDataRemover;
import com.gosqo.flyinheron.global.utility.RespondedCookie;
import com.gosqo.flyinheron.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

class MemberTest extends SpringBootTestBase {
    private final MemberRepository memberRepository;

    @Autowired
    MemberTest(
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

    @Test
    void when_register_member_receive_tokens() {
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

        ResponseEntity<MemberRegisterResponse> response = template.exchange(request, MemberRegisterResponse.class);

        HttpHeaders headers = Objects.requireNonNull(response.getHeaders());
        String targetSetCookieValue = RespondedCookie.getCookieValue(headers,
                AuthCookieManager.REFRESH_TOKEN_COOKIE_NAME);

        MemberRegisterResponse responseBody = Objects.requireNonNull(response.getBody());

        assertThat(StringUtils.hasText(responseBody.accessToken())).isTrue();
        assertThat(StringUtils.hasText(targetSetCookieValue)).isTrue();
    }
}
