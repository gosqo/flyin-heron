package com.vong.manidues.member;

import com.vong.manidues.member.dto.ChangeMemberPasswordRequest;
import com.vong.manidues.member.dto.MemberRegisterRequest;
import com.vong.manidues.member.dto.MemberVerificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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

    private boolean isDuplicated(MemberRegisterRequest request) {
        return memberRepository.findByEmail(request.getEmail()).isPresent()
                && memberRepository.findByNickname(request.getNickname()).isPresent();
    }
}
