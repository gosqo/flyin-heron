package com.gosqo.flyinheron.service;

import com.gosqo.flyinheron.domain.Member;
import com.gosqo.flyinheron.dto.member.*;
import com.gosqo.flyinheron.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean verifyOneself(MemberVerificationRequest request) {
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow();

        return passwordEncoder.matches(request.getPassword(), member.getPassword());
    }

    @Override
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

    @Override
    public void register(MemberRegisterRequest request) {
        if (isDuplicated(request))
            throw new DataIntegrityViolationException("존재하는 자원과 중복.");

        final Member member = request.toEntity(passwordEncoder.encode(request.getPassword()));
        memberRepository.save(member);
    }

    @Override
    public IsUniqueEmailResponse isUniqueEmail(IsUniqueEmailRequest request) {
        if (memberRepository.findByEmail(request.getEmail()).isPresent())
            throw new DataIntegrityViolationException("존재하는 이메일.");

        return IsUniqueEmailResponse.builder()
                .status(HttpStatus.OK.value())
                .message("사용 가능한 이메일 주소입니다.")
                .build();
    }

    @Override
    public IsUniqueNicknameResponse isUniqueNickname(IsUniqueNicknameRequest request) {
        if (memberRepository.findByNickname(request.getNickname()).isPresent())
            throw new DataIntegrityViolationException("존재하는 닉네임.");

        return IsUniqueNicknameResponse.builder()
                .status(HttpStatus.OK.value())
                .message("사용 가능한 닉네임입니다.")
                .build();
    }

    private boolean isDuplicated(MemberRegisterRequest request) {
        return memberRepository.findByEmail(request.getEmail()).isPresent()
                && memberRepository.findByNickname(request.getNickname()).isPresent();
    }
}
