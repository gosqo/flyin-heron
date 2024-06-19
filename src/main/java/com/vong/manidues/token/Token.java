package com.vong.manidues.token;

import com.vong.manidues.member.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@ToString
@EqualsAndHashCode
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String token;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
}
