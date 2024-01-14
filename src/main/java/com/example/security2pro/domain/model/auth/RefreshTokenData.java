package com.example.security2pro.domain.model.auth;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

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

    private String roles;

    @Column(name = "refresh_token_string")
    private String refreshTokenString;


    public RefreshTokenData(String username, Date expiryDate, List<String> roles, String refreshToken) {
        this.username = username;
        this.expiryDate = expiryDate;
        this.roles = String.join(",",roles);
        this.refreshTokenString = refreshToken;
    }


}
