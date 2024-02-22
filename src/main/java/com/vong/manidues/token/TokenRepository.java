package com.vong.manidues.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    @Query("""
            SELECT t
              FROM Token t
             INNER JOIN Member m
                   ON t.member.id = m.id
             WHERE m.id = :memberId AND (t.expired = false OR t.revoked = false)
            """)
    List<Token> findAllValidTokensByMember(@Param("memberId") Long memberId);

    Optional<Token> findByToken(String token);

}
