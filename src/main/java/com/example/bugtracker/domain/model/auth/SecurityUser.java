package com.example.bugtracker.domain.model.auth;


import com.example.bugtracker.domain.enums.UserRole;
import com.example.bugtracker.domain.model.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class SecurityUser implements UserDetails, Serializable {
    // A 'UserDetails' implementation used as the 'Principal' of 'Authentication' objects

    private final String password;

    private final String username;

    private final Set<UserRole> authorities;

    private final boolean enabled;

    public SecurityUser(User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.authorities = user.getAuthorities();
        this.enabled = user.isEnabled();
    }

    public SecurityUser(String username, String password, Collection<? extends GrantedAuthority> authorities, boolean enabled) {
        this.username = username;
        this.password = password;
        this.authorities = authorities.stream()
                .map(authority -> UserRole.valueOf(authority.getAuthority()))
                .collect(Collectors.toCollection(HashSet::new));
        this.enabled = enabled;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities.stream()
                .map(authority -> new SimpleGrantedAuthority(authority.name()))
                .collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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
        return enabled;
    }
}
