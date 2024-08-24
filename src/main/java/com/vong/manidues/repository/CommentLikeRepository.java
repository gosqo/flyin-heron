package com.vong.manidues.repository;

import com.vong.manidues.domain.CommentLike;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    /**
     * 불필요한 조인 제거를 위한 JPQL
     */
    @Query(
            """
                    SELECT cl
                    FROM CommentLike cl
                    WHERE cl.member.id = :memberId
                    AND cl.comment.id = :commentId
                    """
    )
    Optional<CommentLike> findByMemberIdAndCommentId(
            @Param("memberId") Long memberId
            , @Param("commentId") Long commentId
    );

    Slice<CommentLike> findByMemberId(Long memberId, Pageable pageable);
}
