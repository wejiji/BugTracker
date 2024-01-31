package com.example.security2pro.domain.model.auth;


import com.example.security2pro.domain.model.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.Date;

@Getter
@Entity
@Table(name = "refresh_token")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//NamedEntityGraph to efficiently fetch 'User' and its authorities, avoiding the N+1 problem.
@NamedEntityGraph(name="RefreshTokenData.withUser", attributeNodes ={
        @NamedAttributeNode(value = "user", subgraph = "user")},
        subgraphs = @NamedSubgraph(name="user", attributeNodes={
                @NamedAttributeNode("authorities")
        })
)
public class RefreshTokenData {

    /*
     * User's data for refresh token authentication.
     * The user's authorities are fetched every time a new token is issued
     * along with 'RefreshTokenData'.
     * This ensures that tokens are consistently issued with up-to-date roles
     * , reflecting the user's current permissions.
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="username",referencedColumnName = "username")
    private User user;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expiry_date")
    private Date expiryDate;

    @Column(name = "refresh_token_string")
    private String refreshTokenString;

    public void update(User user, Date expiryDate,
                       String refreshToken) {

        this.user = user;
        this.expiryDate = expiryDate;
        this.refreshTokenString = refreshToken;
    }

    public RefreshTokenData(Long id, User user, Date expiryDate,
                            String refreshToken) {
        this.id = id;
        this.user = user;
        this.expiryDate = expiryDate;
        this.refreshTokenString = refreshToken;
    }

    public boolean checkExpiration(Instant instant) {
        return instant.isBefore(expiryDate.toInstant());
    }

}
