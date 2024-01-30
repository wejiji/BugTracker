package com.example.security2pro.authentication.refresh;

import com.example.security2pro.domain.model.auth.SecurityUser;
import jakarta.servlet.http.Cookie;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Objects;

public class RefreshTokenAuthentication extends AbstractAuthenticationToken {

    private SecurityUser user;
    // Must implement 'UserDetails' for compatibility with Spring Security
    // as Spring Security saves 'UserDetails' in 'Authentication' to authenticate a user.
    // 'SecurityUser' implements 'UserDetails'.
    private final Cookie refreshToken;

    public RefreshTokenAuthentication(Cookie refreshToken){
        super(null);
        this.refreshToken = refreshToken;
        setAuthenticated(false);
    }

    public RefreshTokenAuthentication(String username, Cookie refreshToken, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.refreshToken = refreshToken;
        this.user= new SecurityUser(username,refreshToken.getValue(),authorities,true);
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return refreshToken;
    }

    @Override
    public Object getPrincipal() {
        return user;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        RefreshTokenAuthentication that = (RefreshTokenAuthentication) object;
        return Objects.equals(user, that.user) && Objects.equals(refreshToken, that.refreshToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), user, refreshToken);
    }
}
