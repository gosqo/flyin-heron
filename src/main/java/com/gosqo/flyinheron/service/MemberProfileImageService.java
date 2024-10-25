package com.gosqo.flyinheron.service;

import com.gosqo.flyinheron.domain.Member;
import com.gosqo.flyinheron.domain.MemberProfileImage;
import com.gosqo.flyinheron.global.exception.ThrowIf;
import com.gosqo.flyinheron.repository.MemberProfileImageRepository;
import com.gosqo.flyinheron.repository.MemberRepository;
import com.gosqo.flyinheron.repository.jpaentity.MemberProfileImageJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class MemberProfileImageService {
    private final MemberProfileImageRepository memberProfileImageRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void removeMemberProfileImage(String requesterEmail, Long resourceOwnerId) throws IOException {
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

        MemberProfileImageJpaEntity defaultImageJpaEntity = defaultImage.toEntity();

        formerProfileImageJpaEntity.updateImage(defaultImageJpaEntity);
    }

    @Transactional
    public void updateMemberProfileImage(MultipartFile file, String requesterEmail, Long resourceOwnerId) throws IOException {
        Member requester = memberRepository.findByEmail(requesterEmail).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 멤버의 프로필 이미지 변경 요청")
        );

        ThrowIf.NotMatchedResourceOwner(requester, resourceOwnerId);

        MemberProfileImageJpaEntity formerProfileImageJpaEntity =
                memberProfileImageRepository.findById(requester.getProfileImage().getId()).orElseThrow(
                        () -> new NoSuchElementException("존재하지 않는 프로필 이미지 변경 요청")
                );

        MemberProfileImage image = MemberProfileImage.builder()
                .member(requester)
                .inputStream(file.getInputStream())
                .originalFilename(file.getOriginalFilename())
                .build();
        image.saveLocal();

        MemberProfileImageJpaEntity newProfileImageJpaEntity = image.toEntity();

        formerProfileImageJpaEntity.updateImage(newProfileImageJpaEntity);
    }
}
