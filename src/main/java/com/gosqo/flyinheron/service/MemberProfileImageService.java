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

        MemberProfileImage domainToStore = MemberProfileImage.createDefaultImage(member);
        domainToStore.saveLocal();

        MemberProfileImageJpaEntity entity = domainToStore.toEntity();
        member.updateProfileImage(entity);
        memberProfileImageRepository.save(entity);
    }

    public void registerMemberProfileImage(MultipartFile file, String memberEmail) throws IOException {
        Member member = memberRepository.findByEmail(memberEmail).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 멤버에 대한 프로필 이미지 등록 요청")
        );

        MemberProfileImage image = MemberProfileImage.builder()
                .inputStream(file.getInputStream())
                .originalFilename(file.getOriginalFilename())
                .member(member)
                .build();

        image.saveLocal();

        MemberProfileImageJpaEntity entity = image.toEntity();

        memberProfileImageRepository.save(entity);
    }
}
