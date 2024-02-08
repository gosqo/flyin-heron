package com.vong.manidues.member;

import com.vong.manidues.member.dto.MemberDTO;
import com.vong.manidues.member.dto.MemberRegisterRequest;
import com.vong.manidues.utility.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.KeyFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.security.KeyFactorySpi;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    @Value("${jwt.token.secret}")
    private String secretKey;

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final long expireTimeMs = 1000 * 60 * 60 * 24 * 7;

    @Override
    public MemberDTO register(MemberRegisterRequest request) {
        memberRepository.findByEmail(request.getEmail())
                .ifPresent(member -> {
                    throw new RuntimeException();
                });

        Member saveMember = memberRepository.save(request.toEntity(bCryptPasswordEncoder.encode(request.getPassword())));
        return MemberDTO.fromEntity(saveMember);
    }

    @Override
    public String login(String email, String password) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Member using the email does not exist."));

        if (!bCryptPasswordEncoder.matches(password, member.getPassword())) {
            throw new RuntimeException("Password does not match.");
        }

        return JwtUtil.createToken(email, expireTimeMs, secretKey);
    }
}
