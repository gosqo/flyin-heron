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
            HttpServletRequest request,
            @PathVariable("memberId") Long memberId
            , @RequestParam("profileImage") MultipartFile profileImage
    ) throws IOException {
        String accessToken = AuthHeaderUtility.extractAccessToken(request);
        String memberEmail = extractor.extractUserEmail(accessToken);

        memberProfileImageService.updateMemberProfileImage(profileImage, memberEmail, memberId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(JsonResponse.builder()
                        .status(HttpStatus.CREATED.value())
                        .message("정상적으로 등록 했습니다.")
                        .build()
                );
    }
}
