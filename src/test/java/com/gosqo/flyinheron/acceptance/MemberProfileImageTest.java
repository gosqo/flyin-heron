package com.gosqo.flyinheron.acceptance;

import com.gosqo.flyinheron.dto.JsonResponse;
import com.gosqo.flyinheron.dto.board.BoardGetResponse;
import com.gosqo.flyinheron.global.data.TestDataRemover;
import com.gosqo.flyinheron.global.data.TestImageCreator;
import com.gosqo.flyinheron.global.utility.HeadersUtility;
import com.gosqo.flyinheron.repository.BoardRepository;
import com.gosqo.flyinheron.repository.MemberProfileImageRepository;
import com.gosqo.flyinheron.repository.MemberRepository;
import com.gosqo.flyinheron.service.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.util.Objects;

import static com.gosqo.flyinheron.global.utility.HeadersUtility.buildMultipartHeaders;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class MemberProfileImageTest extends SpringBootTestBase {
    private final MemberRepository memberRepository;
    private final MemberProfileImageRepository memberProfileImageRepository;
    private final BoardRepository boardRepository;
    private final JwtService jwtService;
    private String endPoint;

    @Autowired
    MemberProfileImageTest(
            TestRestTemplate template
            , TestDataRemover remover
            , JwtService jwtService
            , MemberRepository memberRepository
            , MemberProfileImageRepository memberProfileImageRepository
            , BoardRepository boardRepository
    ) {
        super(template, remover);
        this.memberRepository = memberRepository;
        this.memberProfileImageRepository = memberProfileImageRepository;
        this.jwtService = jwtService;
        this.boardRepository = boardRepository;
    }

    @Nested
    class When_Profile_Image_Jpa_Entity_Not_Exists {
        @BeforeEach
        void setUp() {
            member = memberRepository.save(buildMember());
            endPoint = "/api/v1/member/" + member.getId() + "/profile/image";
        }

        @Test
        void register_newImage() {
            String accessToken = jwtService.generateAccessToken(member);
            HttpHeaders headers = new HttpHeaders();
            MultiValueMap<String, FileSystemResource> formBody = new LinkedMultiValueMap<>();
            File testImage = TestImageCreator.createTestImage(100, 100, "Test Image");

            formBody.add("profileImage", new FileSystemResource(testImage));

            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            RequestEntity<MultiValueMap<String, FileSystemResource>> requestEntity = RequestEntity
                    .post(endPoint)
                    .headers(headers)
                    .body(formBody);

            // when
            ResponseEntity<JsonResponse> response = template.exchange(requestEntity, JsonResponse.class);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        }
    }

    @Nested
    class When_Profile_Image_Jpa_Entity_Exists {
        @BeforeEach
        void setUp() {
            member = memberRepository.save(buildMember());
            profileImageJpaEntity = memberProfileImageRepository.save(buildProfileImageJpaEntity());
            endPoint = "/api/v1/member/" + member.getId() + "/profile/image";
        }

        @Test
        void register_success_case() {
            // given
            String accessToken = jwtService.generateAccessToken(member);
            String bearerAccessToken = "Bearer " + accessToken;
            HttpHeaders headers = buildMultipartHeaders();
            File testImage = TestImageCreator.createTestImage(100, 100, "Test Image");
            MultiValueMap<String, FileSystemResource> formBody = new LinkedMultiValueMap<>();

            headers.add(HttpHeaders.AUTHORIZATION, bearerAccessToken);
            formBody.add("profileImage", new FileSystemResource(testImage));

            RequestEntity<MultiValueMap<String, FileSystemResource>> requestEntity = RequestEntity
                    .post(endPoint)
                    .headers(headers)
                    .body(formBody);

            // when
            ResponseEntity<JsonResponse> response = template.exchange(requestEntity, JsonResponse.class);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        }

        @Test
        void remove_success_case() {
            // given
            String accessToken = jwtService.generateAccessToken(member);
            HttpHeaders headers = HeadersUtility.buildHeadersWithToken(accessToken);

            RequestEntity<Void> request = RequestEntity
                    .delete(endPoint)
                    .headers(headers)
                    .build();

            // when
            ResponseEntity<JsonResponse> response = template.exchange(request, JsonResponse.class);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Nested
        class Get_Board_Response {

            @BeforeEach
            void setUp() {
                boards = boardRepository.saveAll(buildBoards());
            }

            @Test
            void includes_member_profile_image() {
                RequestEntity<Void> request = RequestEntity
                        .get("/api/v1/board/" + boards.get(0).getId())
                        .build();

                ResponseEntity<BoardGetResponse> response = template.exchange(request, BoardGetResponse.class);
                BoardGetResponse responseBody = Objects.requireNonNull(response.getBody());

                assertThat(responseBody.getMember().profileImage().referencePath())
                        .contains(profileImageJpaEntity.getRenamedFilename());
            }
        }
    }
}
