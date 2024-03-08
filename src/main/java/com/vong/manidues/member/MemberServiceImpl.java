package com.vong.manidues.member;

import com.vong.manidues.member.dto.MemberRegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
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

        Member member = request.toEntity(passwordEncoder.encode(request.getPassword()));
        memberRepository.save(member);

        return true;
    }

}
