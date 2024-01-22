package com.example.security2pro.domain.model.auth;

import com.example.security2pro.domain.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@Table(name ="refresh_token")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshTokenData {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_id")
    private Long id;

    private String username;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expiry_date")
    private Date expiryDate;

    //only for UserRole values for now
    private String roles;

    @Column(name = "refresh_token_string")
    private String refreshTokenString;


    public RefreshTokenData(String username, Date expiryDate, List<String> roles, String refreshToken) {
        // no validation is done in this method
        // all the validations will be done ahead

        Set<String> userRoles = Arrays.stream(UserRole.values()).map(Enum::name).collect(Collectors.toSet());
        if(!userRoles.containsAll(roles)){
            throw new IllegalArgumentException("only user roles can be passed for refresh token data authority");
        }

        this.username = username;
        this.expiryDate = expiryDate;
        this.roles = String.join(",",roles);
        this.refreshTokenString = refreshToken;
    }

    public void update(String username, Date expiryDate, List<String> roles, String refreshToken) {
        // no validation is done in this method
        // all the validations will be done ahead

        Set<String> userRoles = Arrays.stream(UserRole.values()).map(Enum::name).collect(Collectors.toSet());
        if(!userRoles.containsAll(roles)){
            throw new IllegalArgumentException("only user roles can be passed for refresh token data authority");
        }

        this.username = username;
        this.expiryDate = expiryDate;
        this.roles = String.join(",",roles);
        this.refreshTokenString = refreshToken;
    }

    public boolean checkExpiration(Instant instant){
        if(instant.isBefore(expiryDate.toInstant())){
            return true;
        }
        return false;
    }



}
