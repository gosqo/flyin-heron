package com.gosqo.flyinheron.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Board {
    private static final Long DEFAULT_VIEW_COUNT_VALUE = 0L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE)
    private List<Comment> comments;

    @ManyToOne(fetch = FetchType.LAZY)
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

    @PrePersist
    private void prePersist() {
        this.viewCount = DEFAULT_VIEW_COUNT_VALUE;
    }

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
