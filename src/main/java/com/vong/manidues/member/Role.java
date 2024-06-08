package com.vong.manidues.member;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.vong.manidues.member.Permission.*;

@Getter
@RequiredArgsConstructor
public enum Role {
    USER(Collections.emptySet())

    , ADMIN(
            Set.of(
                    ADMIN_READ
                    , ADMIN_CREATE
                    , ADMIN_UPDATE
                    , ADMIN_DELETE
                    , MANAGER_READ
                    , MANAGER_CREATE
                    , MANAGER_UPDATE
                    , MANAGER_DELETE
            )
    )
    , MANAGER(
            Set.of(
                    MANAGER_READ
                    , MANAGER_CREATE
                    , MANAGER_UPDATE
                    , MANAGER_DELETE
            )
    );

    private final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>(getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.name()))
                .toList());

        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}
