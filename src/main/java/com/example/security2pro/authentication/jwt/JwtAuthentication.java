//package com.example.security2pro.authentication.jwt;
//
//import com.example.security2pro.domain.model.auth.SecurityUser;
//import lombok.Getter;
//import org.springframework.security.authentication.AbstractAuthenticationToken;
//import org.springframework.security.core.GrantedAuthority;
//
//import java.util.Collection;
//
//
//@Getter
//public class JwtAuthentication extends AbstractAuthenticationToken {
//
//    private final String jwt;
//    private SecurityUser user;
//
//    public JwtAuthentication(String jwt){
//        super(null);
//        this.jwt = jwt;
//        setAuthenticated(false);
//    }
//
//    public JwtAuthentication(String username, String jwt, Collection<?extends GrantedAuthority > authorities){
//        super(authorities);
//        this.user = new SecurityUser(username, jwt, authorities,true);
//        this.jwt = jwt;
//        setAuthenticated(true);
//    }
//
//
//    @Override
//    public Object getCredentials() {
//        return jwt;
//    }
//
//    @Override
//    public Object getPrincipal() {
//        return user;
//    }
//
//
//
//}
