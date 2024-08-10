package com.vong.manidues.domain.comment;

import com.vong.manidues.domain.board.Board;
import com.vong.manidues.domain.member.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
            targetEntity = Member.class
            , fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "member_id"
            , nullable = false
            , referencedColumnName = "id"
            , foreignKey = @ForeignKey(name = "fk_comment_member_member_id")
    )
    private Member member;

    @ManyToOne(
            targetEntity = Board.class
            , fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "board_id"
            , nullable = false
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
//    @Generated
    @Column(nullable = false)
//    @Column(columnDefinition = "bigint default 0 not null")
    private Long likeCount;

    @CreationTimestamp
    private LocalDateTime registerDate;

    @CreationTimestamp
    private LocalDateTime updateDate;

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", member=" + member +
                ", board=" + board +
                ", content='" + content + '\'' +
                ", likeCount=" + likeCount +
                ", registerDate=" + registerDate +
                ", updateDate=" + updateDate +
                '}';
    }

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
