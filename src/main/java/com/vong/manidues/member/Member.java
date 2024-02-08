package com.vong.manidues.member;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            nullable = false
            , length = 20
            , unique = true
    )
    private String email;

    @Column(
            nullable = false
            , length = 200
    )
    private String password;

    @Column(
            length = 30
            , nullable = false
            , unique = true
    )
    private String nickname;

    @CreationTimestamp
    private LocalDateTime registerDate;


}
