package com.gosqo.flyinheron.repository;

import com.gosqo.flyinheron.repository.jpaentity.MemberProfileImageJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberProfileImageRepository extends JpaRepository<MemberProfileImageJpaEntity, String> {

    Optional<MemberProfileImageJpaEntity> findByMemberId(Long memberId);
}
