package com.example.bugtracker.smalltest.domain.model;

import com.example.bugtracker.databuilders.UserTestDataBuilder;
import com.example.bugtracker.domain.enums.UserRole;
import com.example.bugtracker.domain.model.User;
import com.example.bugtracker.domain.model.auth.RefreshTokenData;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RefreshTokenDataTest {
    /*
     * Clock can be set to any fixed instant in this test, whether in the future or the past.
     * Refresh token will not be considered expired
     * as long as its expiry date is later the given point in time
     */


    // fixed clock to test expiry date
    private final Clock clock
            = Clock.fixed(ZonedDateTime.of(
                    2023,
                    1,
                    1,
                    1,
                    10,
                    10,
                    1,
                    ZoneId.systemDefault()).toInstant(),
            ZoneId.systemDefault());

    @Test
    void RefreshTokenData_createsAndReturnsRefreshTokenData_givenFieldValues() {
        User user = new UserTestDataBuilder()
                .withUsername("refreshUser")
                .withAuthorities(Set.of(UserRole.ROLE_TEAM_TEST, UserRole.ROLE_TEAM_MEMBER))
                .build();

        RefreshTokenData refreshTokenData = new RefreshTokenData(
                1L
                , user
                , Date.from(clock.instant())
                , "refreshTokenString"
        );


        assertEquals(1L, refreshTokenData.getId());
        assertEquals("refreshTokenString", refreshTokenData.getRefreshTokenString());
        assertThat(refreshTokenData.getUser())
                .usingRecursiveComparison()
                .isEqualTo(user);
        assertEquals(Date.from(clock.instant()), refreshTokenData.getExpiryDate());
    }


    @Test
    void update_updatesRefreshTokenData_givenFieldValues() {
        //Setup
        User user = new UserTestDataBuilder().withUsername("originalUser").build();
        RefreshTokenData refreshTokenData = new RefreshTokenData(
                1L
                , user
                , Date.from(clock.instant())
                , "refreshTokenString"
        );

        Date updatedDate = Date.from(clock.instant().plus(1, ChronoUnit.DAYS));
        User updatedUser = new UserTestDataBuilder().withUsername("updatedUser").build();
        // Execution
        refreshTokenData.update(
                updatedUser
                , updatedDate
                , "updatedRefreshTokenString");

        //Assertions
        assertEquals(1L, refreshTokenData.getId());
        assertThat(refreshTokenData.getUser())
                .usingRecursiveComparison()
                .isEqualTo(updatedUser);
        assertEquals(updatedDate, refreshTokenData.getExpiryDate());
        assertEquals("updatedRefreshTokenString", refreshTokenData.getRefreshTokenString());
    }


    @Test
    void checkExpiration_returnsFalse_givenExpiredRefreshToken() {
        /*
         * tests if false is returned
         * when refresh token's expiry date is earlier than the given date
         */

        //Setup
        User user = new UserTestDataBuilder().build();
        RefreshTokenData refreshTokenData = new RefreshTokenData(
                1L
                , user
                , Date.from(clock.instant())//earlier than the given date
                , "refreshTokenString"
        );

        //Execution
        boolean valid = refreshTokenData.checkExpiration(
                clock.instant().plus(1, ChronoUnit.MINUTES));//given date

        //Assertions
        assertFalse(valid);
    }

    @Test
    void checkExpiration_returnsTrue_givenNonExpiredRefreshToken() {
        /*
         * tests if true is returned
         * when refresh token's expiry date is later than the given date
         */

        //Setup
        User user = new UserTestDataBuilder().build();
        RefreshTokenData refreshTokenData = new RefreshTokenData(
                1L
                , user
                , Date.from(clock.instant()) // later than the given date
                , "refreshTokenString"
        );

        //Execution
        boolean valid = refreshTokenData.checkExpiration(
                clock.instant().minus(1, ChronoUnit.MINUTES)); //given date

        //Assertions
        assertTrue(valid);
    }


}
