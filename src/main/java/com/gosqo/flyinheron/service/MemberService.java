package com.gosqo.flyinheron.service;

import com.gosqo.flyinheron.domain.Member;
import com.gosqo.flyinheron.dto.member.*;
import com.gosqo.flyinheron.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

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

    public void register(MemberRegisterRequest request) {
        if (isDuplicated(request))
            throw new DataIntegrityViolationException("존재하는 자원과 중복.");

        final Member member = request.toEntity(passwordEncoder.encode(request.getPassword()));
        memberRepository.save(member);
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
