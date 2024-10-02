package com.gosqo.flyinheron.domain;

import com.gosqo.flyinheron.domain.member.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<Token> tokens;

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<Board> boards;

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<Comment> comments;

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<CommentLike> commentLikes;

    @Column(
            nullable = false
            , length = 50
            , unique = true
    )
    private String email;

    @Column(
            nullable = false
            , length = 200
    )
    private String password;

    @Column(
            length = 20
            , nullable = false
            , unique = true
    )
    private String nickname;

    @CreationTimestamp
    private LocalDateTime registerDate;

    @Enumerated(EnumType.STRING)
    private Role role;

    public void updatePassword(String changedPassword) {
        this.password = changedPassword;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return List.of(new SimpleGrantedAuthority(role.name()));
        return role.getAuthorities();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
