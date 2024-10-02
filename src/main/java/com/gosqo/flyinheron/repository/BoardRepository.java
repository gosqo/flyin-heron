package com.gosqo.flyinheron.repository;

import com.gosqo.flyinheron.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("""
            SELECT b, m
              FROM Board b
             INNER JOIN Member m
                   ON b.member.id = m.id
             WHERE m.nickname = :writer
            """)
    List<Board> findAllByWriter(@Param("writer") String writer);

    @Query("""
            SELECT b, m
              FROM Board b
             INNER JOIN Member m
                   ON b.member.id = m.id
             WHERE m.nickname LIKE %:keyword%
            """)
    List<Board> findAllByKeywordLikeWriter(@NonNull @Param("keyword") String keyword);

    @Query("""
            SELECT b, m
              FROM Board b
             INNER JOIN Member m
                   ON b.member.id = m.id
             WHERE b.title LIKE %:keyword%
            """)
    List<Board> findAllByKeywordLikeTitle(@NonNull @Param("keyword") String keyword);

    @Query("""
            SELECT b, m
              FROM Board b
             INNER JOIN Member m
                   ON b.member.id = m.id
             WHERE b.content LIKE %:keyword%
            """)
    List<Board> findAllByKeywordLikeContent(@Param("keyword") String keyword);
}
