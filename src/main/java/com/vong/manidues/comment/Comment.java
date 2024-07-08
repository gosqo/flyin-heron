package com.vong.manidues.comment;

import com.vong.manidues.board.Board;
import com.vong.manidues.member.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(
            name = "member_id"
            , referencedColumnName = "id"
            , foreignKey = @ForeignKey(name = "fk_comment_member_member_id")
    )
    private Member member;

    @ManyToOne
    @JoinColumn(
            name = "board_id"
            , referencedColumnName = "id"
            , foreignKey = @ForeignKey(name = "fk_comment_board_board_id")
    )
    private Board board;

    @Column(
            nullable = false
            , length = 4000
    )
    private String content;

    @ColumnDefault(value = "0")
    @Column(nullable = false)
//    @Column(columnDefinition = "bigint default 0 not null")
    private Long likeCount;

    @CreationTimestamp
    private LocalDateTime registerDate;

    @CreationTimestamp
    private LocalDateTime updateDate;


    public void updateContent(String content) {
        this.content = content;
    }

    public void addLikeCount() {
        this.likeCount++;
    }

    public void subtractLikeCount() {
        this.likeCount--;
    }

    public void updateUpdateDate() {
        this.updateDate = LocalDateTime.now();
    }
}
