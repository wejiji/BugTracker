package com.example.security2pro.domain.model.auth;


import com.example.security2pro.domain.enums.UserRole;
import com.example.security2pro.domain.model.User;
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

    //private final User user;

    private String password;

    private String username;

    private Set<UserRole> authorities;

    private boolean enabled;

    public SecurityUser(User user){
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.authorities= user.getAuthorities();
        this.enabled = user.isEnabled();
    }

    public SecurityUser(String username, String password, Collection<? extends GrantedAuthority>  authorities,boolean enabled){
        this.username = username;
        this.password = password;
        this.authorities= authorities.stream().map((authority)->UserRole.valueOf(authority.getAuthority())).collect(Collectors.toCollection(HashSet::new));
        this.enabled = enabled;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities.stream().map((authority)->new SimpleGrantedAuthority(authority.name())).collect(Collectors.toSet());
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
