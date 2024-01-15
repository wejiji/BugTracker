package com.example.security2pro.domain;

import com.example.security2pro.domain.enums.Role;
import com.example.security2pro.domain.model.User;
import com.example.security2pro.domain.model.auth.SecurityUser;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SecurityUserTest {


    @Test
    public void test(){

        User user= User.createUser(
                1L
                ,"testUsername"
                ,"testPassword"
                ,"testFirstName"
                ,"testLastName"
                ,"test@gmail.com"
                , Set.of(Role.ROLE_TEAM_MEMBER)
                ,true
        );

        SecurityUser securityUser = new SecurityUser(user);

        assertEquals("testPassword", securityUser.getPassword());
        assertEquals("testUsername", securityUser.getUsername());
        assertTrue(securityUser.isAccountNonExpired());
        assertTrue(securityUser.isAccountNonLocked());
        assertTrue(securityUser.isCredentialsNonExpired());
        assertEquals(user.isEnabled(), securityUser.isEnabled());
        Collection<? extends GrantedAuthority> expectedAuthorities= user.getAuthorities().stream().map(auth->new SimpleGrantedAuthority(auth.name())).collect(Collectors.toCollection(ArrayList::new));
        assertEquals(expectedAuthorities, securityUser.getAuthorities());
    }







}
