package com.gosqo.flyinheron.service;

import com.gosqo.flyinheron.domain.Member;
import com.gosqo.flyinheron.domain.MemberProfileImage;
import com.gosqo.flyinheron.global.exception.ThrowIf;
import com.gosqo.flyinheron.repository.MemberProfileImageRepository;
import com.gosqo.flyinheron.repository.MemberRepository;
import com.gosqo.flyinheron.repository.jpaentity.MemberProfileImageJpaEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberProfileImageService {
    private final MemberProfileImageRepository memberProfileImageRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void removeMemberProfileImage(String requesterEmail, Long resourceOwnerId) {
        Member requester = memberRepository.findByEmail(requesterEmail).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 멤버의 프로필 이미지 삭제 요청")
        );

        ThrowIf.NotMatchedResourceOwner(requester, resourceOwnerId);

        MemberProfileImageJpaEntity formerProfileImageJpaEntity =
                memberProfileImageRepository.findById(requester.getProfileImage().getId()).orElseThrow(
                        () -> new NoSuchElementException("존재하지 않는 프로필 이미지 변경 요청")
                );

        MemberProfileImage defaultImage = MemberProfileImage.createDefaultImage(requester);
        defaultImage.saveLocal();

        MemberProfileImageJpaEntity defaultImageJpaEntity = MemberProfileImageJpaEntity.of(defaultImage);

        formerProfileImageJpaEntity.updateImage(defaultImageJpaEntity);
    }

    @Transactional
    public void updateMemberProfileImage (MultipartFile file, String requesterEmail, Long resourceOwnerId) {
        Member requester = memberRepository.findByEmail(requesterEmail).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 멤버의 프로필 이미지 변경 요청")
        );

        ThrowIf.NotMatchedResourceOwner(requester, resourceOwnerId);

        MemberProfileImage image = convertToProfileImage(requester, file);
        image.saveLocal();

        memberProfileImageRepository.findByMemberId(requester.getId()).ifPresentOrElse(item ->
                {
                    MemberProfileImageJpaEntity newImageEntity = MemberProfileImageJpaEntity.of(image);
                    item.updateImage(newImageEntity);
                }
                , () -> {
                    MemberProfileImageJpaEntity profileImageJpaEntity = MemberProfileImageJpaEntity.of(image);
                    memberProfileImageRepository.save(profileImageJpaEntity);
                }
        );
    }

    private static MemberProfileImage convertToProfileImage(Member member, MultipartFile file) {
        MemberProfileImage image;

        try {
            image = MemberProfileImage.builder()
                    .member(member)
                    .inputStream(file.getInputStream())
                    .originalFilename(file.getOriginalFilename())
                    .build();
        } catch (IOException e) {
            log.error("I/O Error", e);
            throw new RuntimeException(e.getClass().getName() + " " + e.getMessage());
        }

        return image;
    }
}
