package com.gosqo.flyinheron.service;

import com.gosqo.flyinheron.domain.Member;
import com.gosqo.flyinheron.domain.MemberProfileImage;
import com.gosqo.flyinheron.dto.member.*;
import com.gosqo.flyinheron.repository.MemberProfileImageRepository;
import com.gosqo.flyinheron.repository.MemberRepository;
import com.gosqo.flyinheron.repository.jpaentity.MemberProfileImageJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberProfileImageRepository memberProfileImageRepository;

    public boolean verifyOneself(MemberVerificationRequest request) {
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow();

        return passwordEncoder.matches(request.getPassword(), member.getPassword());
    }

    public boolean changePassword(ChangeMemberPasswordRequest request) {
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow();

        if (passwordEncoder.matches(request.getCurrentPassword(), member.getPassword())
                && request.getChangedPassword().equals(request.getChangedPasswordCheck())
        ) {
            member.updatePassword(passwordEncoder.encode(request.getChangedPassword()));
            memberRepository.save(member);

            return true;
        }
        return false;
    }

    @Transactional
    public void register(MemberRegisterRequest request) {
        if (isDuplicated(request)) {
            throw new DataIntegrityViolationException(
                    "회원 가입에 중복되는 입력값. 클라이언트에서 유효성 검사 후 통과된 경우로, 확인 필요."
            );
        }

        final Member member = request.toEntity(passwordEncoder.encode(request.getPassword()));
        final Member storedMember = memberRepository.save(member);

        MemberProfileImage defaultImage = MemberProfileImage.createDefaultImage(storedMember);
        defaultImage.saveLocal();
        MemberProfileImageJpaEntity profileImageJpaEntity = MemberProfileImageJpaEntity.of(defaultImage);

        memberProfileImageRepository.save(profileImageJpaEntity);
//        storedMember.updateProfileImage(profileImageJpaEntity);
    }

    public boolean isUniqueEmail(IsUniqueEmailRequest request) {
        return memberRepository.findByEmail(request.getEmail()).isEmpty();
    }

    public boolean isUniqueNickname(IsUniqueNicknameRequest request) {
        return memberRepository.findByNickname(request.getNickname()).isEmpty();
    }

    private boolean isDuplicated(MemberRegisterRequest request) {
        return memberRepository.findByEmail(request.getEmail()).isPresent()
                && memberRepository.findByNickname(request.getNickname()).isPresent();
    }
}
