package com.vong.manidues.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.Objects;

@Entity
@Getter
@ToString
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

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Token token1 = (Token) object;
        return Objects.equals(token, token1.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token);
    }
}
