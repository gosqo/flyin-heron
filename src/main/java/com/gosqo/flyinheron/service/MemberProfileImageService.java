package com.gosqo.flyinheron.service;

import com.gosqo.flyinheron.domain.Member;
import com.gosqo.flyinheron.domain.MemberProfileImage;
import com.gosqo.flyinheron.repository.MemberProfileImageRepository;
import com.gosqo.flyinheron.repository.MemberRepository;
import com.gosqo.flyinheron.repository.jpaentity.MemberProfileImageJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class MemberProfileImageService {
    private final MemberProfileImageRepository memberProfileImageRepository;
    private final MemberRepository memberRepository;

    public void removeMemberProfileImage(String memberEmail) throws IOException {
        Member member = memberRepository.findByEmail(memberEmail).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 멤버에 대한 프로필 이미지 삭제 요청")
        );
        MemberProfileImageJpaEntity formerProfileImageJpaEntity = memberProfileImageRepository.findById(member.getProfileImage().getId()).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 프로필 이미지 변경 요청")
        );

        MemberProfileImage defaultImage = MemberProfileImage.createDefaultImage(member);
        defaultImage.saveLocal();

        MemberProfileImageJpaEntity defaultImageJpaEntity = defaultImage.toEntity();

        formerProfileImageJpaEntity.updateImage(defaultImageJpaEntity);
    }

    public void updateMemberProfileImage(MultipartFile file, String memberEmail) throws IOException {
        Member member = memberRepository.findByEmail(memberEmail).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 멤버에 대한 프로필 이미지 변경 요청")
        );
        MemberProfileImageJpaEntity formerProfileImageJpaEntity = memberProfileImageRepository.findById(member.getProfileImage().getId()).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 프로필 이미지 변경 요청")
        );

        MemberProfileImage image = MemberProfileImage.builder()
                .member(member)
                .inputStream(file.getInputStream())
                .originalFilename(file.getOriginalFilename())
                .build();
        image.saveLocal();

        MemberProfileImageJpaEntity newProfileImageJpaEntity = image.toEntity();

        formerProfileImageJpaEntity.updateImage(newProfileImageJpaEntity);
    }
}
