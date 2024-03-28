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

        if (memberRepository.findByEmail(request.getEmail()).isPresent() ||
        memberRepository.findByNickname(request.getNickname()).isPresent()
        ) {
            return false;
        }

        if (!request.getPassword().equals(request.getPasswordCheck())) {
            return false;
        }

        Member member = request.toEntity(passwordEncoder.encode(request.getPassword()));

        try {

            memberRepository.save(member);
        } catch (DataIntegrityViolationException ex) {
            log.info("DataIntegrityViolationException occurs,\n{}", ex.getMessage());
            return false;
        }

        return true;
    }

}
