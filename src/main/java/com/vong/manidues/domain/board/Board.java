package com.vong.manidues.domain.board;

import com.vong.manidues.domain.comment.Comment;
import com.vong.manidues.domain.member.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@ToString(exclude = {
        "comments"
})
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(
            name = "member_id"
            , referencedColumnName = "id"
    )
    private Member member;

    @OneToMany(mappedBy = "board")
    private List<Comment> comments;

    @Column(
            nullable = false
            , length = 50
    )
    private String title;

    @Column(
            nullable = false
            , length = 4000
    )
    private String content;

    @ColumnDefault(value = "0")
    private Long viewCount;

    @CreationTimestamp
    private LocalDateTime registerDate;

    @CreationTimestamp
    private LocalDateTime updateDate;

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void addViewCount() {
        this.viewCount++;
    }

    public void updateUpdateDate() {
        this.updateDate = LocalDateTime.now();
    }
}
