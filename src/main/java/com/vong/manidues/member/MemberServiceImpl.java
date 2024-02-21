package com.vong.manidues.member;

import com.vong.manidues.member.dto.MemberDTO;
import com.vong.manidues.member.dto.MemberRegisterRequest;
import com.vong.manidues.utility.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final JwtUtil jwtUtil;

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public MemberDTO register(MemberRegisterRequest request) {
        memberRepository.findByEmail(request.getEmail())
                .ifPresent(member -> {
                    throw new RuntimeException();
                });

        Member saveMember = memberRepository.save(request.toEntity(passwordEncoder.encode(request.getPassword())));
        return MemberDTO.fromEntity(saveMember);
    }

    @Override
    public String login(String email, String password) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Member using the email does not exist."));

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new RuntimeException("Password does not match.");
        }

        return jwtUtil.createToken(email);
    }
}
