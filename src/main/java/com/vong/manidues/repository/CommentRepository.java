package com.vong.manidues.repository;

import com.vong.manidues.domain.Comment;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Lock(LockModeType.PESSIMISTIC_READ)
    Slice<Comment> findByBoardId(Long boardId, Pageable pageable);

    @Query(
            """
                    SELECT c
                    FROM Comment c
                    WHERE c.id = :commentId
                    """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Comment> findByIdForUpdate(@Param("commentId") Long commentId);
}
