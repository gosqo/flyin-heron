package com.vong.manidues.domain.board;

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
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(
            name = "member_id"
            , nullable = false
            , referencedColumnName = "id"
    )
    private Member member;

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
