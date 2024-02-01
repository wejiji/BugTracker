package com.example.bugtracker.smalltest.domain;

import com.example.bugtracker.domain.enums.UserRole;
import com.example.bugtracker.domain.model.User;
import com.example.bugtracker.domain.model.auth.SecurityUser;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SecurityUserTest {

    @Test
    void SecurityUser() {
        /*
         * this verifies that 'SecurityUser' constructor correctly
         * creates and returns 'SecurityUser' instance when provided with a 'User' argument
         *
         * Also verifies That the overridden methods' return values are correct.
         * the following methods in 'SecurityUser' are expected to always return true:
         * 'isAccountNonExpired', 'isAccountNonLocked', and 'isCredentialsNonExpired'.
         */

        //Setup
        User user = User.createUser(
                1L
                , "testUsername"
                , "testPassword"
                , "testFirstName"
                , "testLastName"
                , "test@gmail.com"
                , Set.of(UserRole.ROLE_TEAM_MEMBER)
                , true
        );

        //Execution
        SecurityUser securityUser = new SecurityUser(user);

        //Assertions
        assertEquals("testPassword", securityUser.getPassword());
        assertEquals("testUsername", securityUser.getUsername());
        assertTrue(securityUser.isAccountNonExpired());
        assertTrue(securityUser.isAccountNonLocked());
        assertTrue(securityUser.isCredentialsNonExpired());
        assertEquals(user.isEnabled(), securityUser.isEnabled());

        Collection<? extends GrantedAuthority> expectedAuthorities
                = user.getAuthorities()
                .stream()
                .map(auth -> new SimpleGrantedAuthority(auth.name()))
                .collect(Collectors.toCollection(HashSet::new));
        assertEquals(expectedAuthorities, securityUser.getAuthorities());
    }


}
