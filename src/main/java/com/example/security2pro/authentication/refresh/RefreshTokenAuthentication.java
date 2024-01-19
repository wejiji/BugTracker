package com.example.security2pro.authentication.refresh;

import com.example.security2pro.domain.model.auth.SecurityUser;
import jakarta.servlet.http.Cookie;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class RefreshTokenAuthentication extends AbstractAuthenticationToken {

    private SecurityUser user;
    // this field has to be SecurityUser type
    // - spring security save 'UserDetails' in authentication.
    // 'SecurityUser' implements UserDetails
    private final Cookie refreshToken;

    public RefreshTokenAuthentication(Cookie refreshToken){
        super(null);
        this.refreshToken = refreshToken;
        setAuthenticated(false);
    }

    /**
     * Creates a token with the supplied array of authorities.
     *
     * @param authorities the collection of <tt>GrantedAuthority</tt>s for the principal
     *                    represented by this authentication object.
     */
//    public RefreshTokenAuthentication(SecurityUser user, Cookie refreshToken, Collection<? extends GrantedAuthority> authorities) {
//        super(authorities);
//        this.refreshToken = refreshToken;
//        this.user= user;
//        setAuthenticated(true);
//    }

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
}
