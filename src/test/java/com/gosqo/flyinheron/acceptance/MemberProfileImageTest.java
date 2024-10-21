package com.gosqo.flyinheron.acceptance;

import com.gosqo.flyinheron.dto.JsonResponse;
import com.gosqo.flyinheron.global.data.TestDataRemover;
import com.gosqo.flyinheron.global.data.TestImageCreator;
import com.gosqo.flyinheron.repository.MemberRepository;
import com.gosqo.flyinheron.service.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.io.IOException;

import static com.gosqo.flyinheron.global.utility.HttpUtility.buildMultipartPostHeaders;
import static com.gosqo.flyinheron.global.utility.HttpUtility.buildMultipartPostRequestEntity;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class MemberProfileImageTest extends SpringBootTestBase {
    private final MemberRepository memberRepository;
    private final JwtService jwtService;

    @Autowired
    public MemberProfileImageTest(
            TestRestTemplate template
            , MemberRepository memberRepository
            , JwtService jwtService
            , TestDataRemover remover
    ) {
        super(template, remover);
        this.memberRepository = memberRepository;
        this.jwtService = jwtService;
    }

    @BeforeEach
    void setUp() {
        member = memberRepository.save(buildMember());
    }

    @Test
    void register_success_case() throws IOException {
        // given
        String accessToken = jwtService.generateAccessToken(member);
        HttpHeaders headers = buildMultipartPostHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        File testImage = TestImageCreator.createTestImage(100, 100, "Test Image");

        MultiValueMap<String, Object> formBody = new LinkedMultiValueMap<>();
        formBody.add("profileImage", new FileSystemResource(testImage));

        RequestEntity<Object> requestEntity = buildMultipartPostRequestEntity(headers, formBody, "/api/v1/member/" + member.getId() + "/profile/image");

        // when
        ResponseEntity<JsonResponse> response = template.exchange(requestEntity, JsonResponse.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }
}
