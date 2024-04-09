package com.vong.manidues.member;

import com.vong.manidues.member.dto.ChangeMemberPasswordRequest;
import com.vong.manidues.member.dto.MemberRegisterRequest;
import com.vong.manidues.member.dto.MemberVerificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean verifyOneself(MemberVerificationRequest request) {
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow();

        return passwordEncoder.matches(
                request.getPassword(), member.getPassword()
        );
    }

    @Override
    public boolean changePassword(ChangeMemberPasswordRequest request) {
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow();

        if (passwordEncoder.matches(
                request.getCurrentPassword(), member.getPassword())
                && request.getChangedPassword().equals(
                        request.getChangedPasswordCheck())
        ) {
            member.updatePassword(
                    passwordEncoder.encode(request.getChangedPassword()));
            memberRepository.save(member);

            return true;
        }
        return false;
    }

    @SuppressWarnings("null")
    @Override
    public boolean register(MemberRegisterRequest request) {
        if (isDuplicated(request)) {
            return false;
        }

        Member member = request.toEntity(passwordEncoder.encode(request.getPassword()));

        try {
            Member storedMember = memberRepository.save(member);

            log.info("""
                            Member register succeeded.
                                Registered member email is: {}
                            """,
                    storedMember.getEmail()
            );
        } catch (DataIntegrityViolationException ex) {
            log.info("""
                            DataIntegrityViolationException occurs on method register() in memberService,
                                message is: {}
                            """,
                    ex.getMessage()
            );
            return false;
        }
        return true;
    }

    private boolean isDuplicated(MemberRegisterRequest request) {
        return memberRepository.findByEmail(request.getEmail()).isPresent() &&
                memberRepository.findByNickname(request.getNickname()).isPresent();
    }
}
