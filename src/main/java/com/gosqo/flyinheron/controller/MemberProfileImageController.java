package com.gosqo.flyinheron.controller;

import com.gosqo.flyinheron.dto.JsonResponse;
import com.gosqo.flyinheron.global.utility.AuthHeaderUtility;
import com.gosqo.flyinheron.service.ClaimExtractor;
import com.gosqo.flyinheron.service.MemberProfileImageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/member/{memberId}/profile/image")
@RequiredArgsConstructor
public class MemberProfileImageController {

    private final MemberProfileImageService memberProfileImageService;
    private final ClaimExtractor extractor;

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("")
    public ResponseEntity<JsonResponse> registerMemberProfileImage(
            HttpServletRequest request
            , @PathVariable("memberId") Long memberId
            , @RequestParam("profileImage") MultipartFile profileImage
    ) throws IOException {
        String accessToken = AuthHeaderUtility.extractAccessToken(request);
        String memberEmail = extractor.extractUserEmail(accessToken);

        memberProfileImageService.updateMemberProfileImage(profileImage, memberEmail, memberId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(JsonResponse.builder()
                        .status(HttpStatus.CREATED.value())
                        .message("프로필 이미지를 정상적으로 등록했습니다.")
                        .build()
                );
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("")
    public ResponseEntity<JsonResponse> deleteMemberProfileImage(
            HttpServletRequest request
            , @PathVariable("memberId") Long memberId
    ) throws IOException {
        String accessToken = AuthHeaderUtility.extractAccessToken(request);
        String memberEmail = extractor.extractUserEmail(accessToken);

        memberProfileImageService.removeMemberProfileImage(memberEmail, memberId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(JsonResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("프로필 이미지가 삭제됐습니다.")
                        .build()
                );
    }
}
