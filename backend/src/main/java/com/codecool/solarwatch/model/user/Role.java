package com.codecool.solarwatch.model.user;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

import static com.codecool.solarwatch.model.user.Permission.*;

public enum Role {
    USER(Set.of(
            CITY_READ,
            SUNTIMES_READ
    )),
    ADMIN(Set.of(
            CITY_READ,
            CITY_CREATE,
            CITY_UPDATE,
            CITY_DELETE,
            SUNTIMES_READ,
            SUNTIMES_CREATE,
            SUNTIMES_UPDATE,
            SUNTIMES_DELETE
    ));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public Set<SimpleGrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> authorities = getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}
