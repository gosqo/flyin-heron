package com.gosqo.flyinheron.repository;

import com.gosqo.flyinheron.domain.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByToken(String token);
    List<Token> findAllByToken(String token);
    @Transactional
    int deleteByToken(String token);
}
