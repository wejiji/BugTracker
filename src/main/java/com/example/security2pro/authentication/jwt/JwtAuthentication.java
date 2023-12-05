package com.example.security2pro.authentication.jwt;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
public class JwtAuthentication extends AbstractAuthenticationToken {

    private final String jwt;
    private String username;

    public JwtAuthentication(String jwt){
        super(null); //이부분 헷갈림
        this.jwt = jwt;
        setAuthenticated(false);
    }

    public JwtAuthentication(String username, String jwt, Collection<?extends GrantedAuthority > authorities){
        super(authorities);
        this.username = username;
        this.jwt = jwt;
        setAuthenticated(true);
    }


    @Override
    public Object getCredentials() {
        return jwt;
    }

    @Override
    public Object getPrincipal() {
        return username;
    }



}
