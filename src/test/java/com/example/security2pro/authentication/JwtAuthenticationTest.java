//package com.example.security2pro.authentication;
//
//import com.example.security2pro.domain.model.auth.SecurityUser;
//import org.junit.jupiter.api.Test;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//
//import java.util.Set;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.*;
//
//public class JwtAuthenticationTest {
//
//
//
//    @Test
//    public void testConstructor_unAuthenticated(){
//
//        String jwt = "jwtString";
//
//        JwtAuthentication jwtAuthentication
//                = new JwtAuthentication(jwt);
//
//        assertEquals(jwt ,jwtAuthentication.getJwt());
//        assertFalse(jwtAuthentication.isAuthenticated());
//    }
//
//    @Test
//    public void testConstructor_authenticated(){
//
//        String jwt = "jwtString";
//
//        JwtAuthentication jwtAuthentication
//                = new JwtAuthentication("testUsername", jwt, Set.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
//
//        assertEquals(jwt ,jwtAuthentication.getJwt());
//        assertTrue(jwtAuthentication.isAuthenticated());
//        assertEquals("jwtString",jwtAuthentication.getCredentials());
//
//        assertThat(new SecurityUser("testUsername","jwtString", Set.of(new SimpleGrantedAuthority("ROLE_ADMIN")),true))
//                .usingRecursiveComparison().isEqualTo(jwtAuthentication.getPrincipal());
//
//    }
//
//
//
//
//
//
//
//}
