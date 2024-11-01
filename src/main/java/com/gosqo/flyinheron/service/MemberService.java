package com.gosqo.flyinheron.service;

import com.gosqo.flyinheron.domain.Member;
import com.gosqo.flyinheron.domain.MemberProfileImage;
import com.gosqo.flyinheron.domain.Token;
import com.gosqo.flyinheron.dto.member.*;
import com.gosqo.flyinheron.repository.MemberProfileImageRepository;
import com.gosqo.flyinheron.repository.MemberRepository;
import com.gosqo.flyinheron.repository.TokenRepository;
import com.gosqo.flyinheron.repository.jpaentity.MemberProfileImageJpaEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;
    private final ClaimExtractor claimExtractor;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final MemberProfileImageRepository memberProfileImageRepository;
    @PersistenceContext
    private EntityManager em;

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
    public Map<String, String> register(MemberRegisterRequest request) {
        if (isDuplicated(request)) {
            throw new DataIntegrityViolationException(
                    "회원 가입에 중복되는 입력값. 클라이언트에서 유효성 검사 후 통과된 경우로, 확인 필요."
            );
        }

        final Member memberToStore = request.toEntity(passwordEncoder.encode(request.getPassword()));
        final Member storedMember = memberRepository.save(memberToStore);

        MemberProfileImage defaultImage = MemberProfileImage.createDefaultImage(storedMember.toModel());
        defaultImage.saveLocal();
        MemberProfileImageJpaEntity profileImageJpaEntity = MemberProfileImageJpaEntity.of(defaultImage);

        memberProfileImageRepository.save(profileImageJpaEntity); // flush
//        storedMember.updateProfileImage(profileImageJpaEntity);

        final Map<String, String> tokens = new HashMap<>();

        String accessToken = jwtService.buildAccessTokenWithClaims(storedMember);
        String refreshToken = jwtService.generateRefreshToken(storedMember);

        saveMemberToken(storedMember, refreshToken);

        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        return tokens;
    }

    public boolean isUniqueEmail(IsUniqueEmailRequest request) {
        return memberRepository.findByEmail(request.getEmail()).isEmpty();
    }

    public boolean isUniqueNickname(IsUniqueNicknameRequest request) {
        return memberRepository.findByNickname(request.getNickname()).isEmpty();
    }

    private void saveMemberToken(Member member, String jwtToken) {
        final Token token = Token.builder()
                .member(member)
                .token(jwtToken)
                .expirationDate(claimExtractor.extractExpiration(jwtToken))
                .build();
        tokenRepository.save(token);
    }

    private boolean isDuplicated(MemberRegisterRequest request) {
        return memberRepository.findByEmail(request.getEmail()).isPresent()
                && memberRepository.findByNickname(request.getNickname()).isPresent();
    }
}
