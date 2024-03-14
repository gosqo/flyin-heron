package com.vong.manidues.board;

import com.vong.manidues.member.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Board {
//TODO build entity board.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            nullable = false
            , length = 50
    )
    private String title;

    @Column(
            nullable = false
            , length = 1000
    )
    private String content;

    @CreationTimestamp
    private LocalDateTime registerDate;

    @UpdateTimestamp
    private LocalDateTime updateDate;

    @ManyToOne
    @JoinColumn(
            name = "member_id"
            , referencedColumnName = "id"
    )
    private Member member;

//    @PrePersist
//    @PreUpdate
//    private void updateFieldWriter() {
//        this.writer = this.member.getNickname();
//    }

}
