package com.vong.manidues.repository;

import com.vong.manidues.domain.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Slice<Comment> findByBoardId(Long boardId, Pageable pageable);
}
