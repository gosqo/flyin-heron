package com.vong.manidues.member;

import com.vong.manidues.member.dto.MemberRegisterRequest;
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

    @SuppressWarnings("null")
    @Override
    public boolean register(MemberRegisterRequest request) {

        if (!isValidInputs(request)) {
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

    private boolean isValidInputs(MemberRegisterRequest request) {

        return memberRepository.findByEmail(request.getEmail()).isEmpty() &&
                memberRepository.findByNickname(request.getNickname()).isEmpty() &&
                request.getPassword().equals(request.getPasswordCheck());
    }

}
