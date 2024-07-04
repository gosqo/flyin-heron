package com.vong.manidues.token;

import com.vong.manidues.member.Member;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@ToString
@EqualsAndHashCode
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(
        uniqueConstraints = @UniqueConstraint(
                name = "token_unique"
                , columnNames = "token"
        )
)
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    private String token; // Unique

    @Column(nullable = false)
    private Date expirationDate;
}
