package com.gosqo.flyinheron.domain;

import com.gosqo.flyinheron.domain.member.Role;
import com.gosqo.flyinheron.repository.jpaentity.MemberProfileImageJpaEntity;
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

    @OneToOne(mappedBy = "member", cascade = CascadeType.REMOVE)
    private MemberProfileImageJpaEntity profileImage;

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

    public static Member of(MemberModel model) {
        Member entity = new Member();

        entity.id = model.getId();
        entity.email = model.getEmail();
        entity.password = model.getPassword();
        entity.nickname = model.getNickname();
        entity.registerDate = model.getRegisterDate();
        entity.role = model.getRole();

        return entity;
    }

    public MemberModel toModel() {
        MemberProfileImage profileImageModel = this.profileImage == null
                ? null
                : this.profileImage.toModel();

        return MemberModel.builder()
                .id(this.id)
                .profileImage(profileImageModel)
                .email(this.email)
                .password(this.password)
                .nickname(this.nickname)
                .registerDate(this.registerDate)
                .role(this.role)
                .build();
    }

    public void updateProfileImage(MemberProfileImageJpaEntity profileImage) {
        this.profileImage = profileImage;

        if (this.profileImage.getMember() == null) {
            this.profileImage.updateMember(this);
        }
    }

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
