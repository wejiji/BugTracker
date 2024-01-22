package com.example.security2pro.domain.model;

import com.example.security2pro.domain.model.auth.RefreshTokenData;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RefreshTokenDataTest {
    /*
     * Each test of this class will test at most one method of RefreshTokenData class
     * Every test of this class is a small test
     *
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
    void RefreshTokenData_createsAndReturnsRefreshTokenData() {
        // success case
        // tests if roles will be joined into one String

        RefreshTokenData refreshTokenData = new RefreshTokenData(
                "testUsername"
                , Date.from(clock.instant())
                , List.of("ROLE_TEAM_TEST", "ROLE_TEAM_MEMBER")
                , "refreshTokenString"
        );

        assertEquals("refreshTokenString", refreshTokenData.getRefreshTokenString());
        assertEquals("ROLE_TEAM_TEST,ROLE_TEAM_MEMBER", refreshTokenData.getRoles());
        assertEquals("testUsername", refreshTokenData.getUsername());
        assertEquals(Date.from(clock.instant()), refreshTokenData.getExpiryDate());
    }

    @Test
    void RefreshTokenData_throwsException_givenInvalidRole() {
        // 'roles' field it is only used for UserRole values

        //Setup
        Executable refreshTokenDataExecutable = () -> new RefreshTokenData(
                "testUsername"
                , Date.from(clock.instant())
                , List.of("ROLE_PROJECT_TEST")
                , "refreshTokenString"
        );

        //Execution & Assertions
        assertThrows(IllegalArgumentException.class, refreshTokenDataExecutable);
    }

    @Test
    void update_updatesRefreshTokenData() {
        /*
         * success case
         * roles will be joined in one String
         */

        //Setup
        RefreshTokenData refreshTokenData = new RefreshTokenData(
                "testUsername"
                , Date.from(clock.instant())
                , List.of("ROLE_TEAM_MEMBER")
                , "refreshTokenString"
        );

        Date updatedDate = Date.from(clock.instant().plus(1, ChronoUnit.DAYS));
        // Execution
        refreshTokenData.update("updatedUsername"
                , updatedDate
                , List.of("ROLE_TEAM_LEAD", "ROLE_TEAM_TEST")
                , "updatedRefreshTokenString");

        //Assertions
        assertEquals("updatedUsername", refreshTokenData.getUsername());
        assertEquals(updatedDate, refreshTokenData.getExpiryDate());
        assertEquals("updatedRefreshTokenString", refreshTokenData.getRefreshTokenString());
        assertEquals("ROLE_TEAM_LEAD,ROLE_TEAM_TEST", refreshTokenData.getRoles());
    }


    @Test
    void update_throwsException_givenInvalidRole() {
        /*
         * tests if IllegalArgumentException is thrown
         * when a roles other than UserRole is passed
         */

        RefreshTokenData refreshTokenData = new RefreshTokenData(
                "testUsername"
                , Date.from(clock.instant())
                , List.of("ROLE_TEAM_MEMBER")
                , "refreshTokenString"
        );

        Date updatedDate = Date.from(clock.instant().plus(1, ChronoUnit.DAYS));


        //Setup
        Executable refreshTokenDataExecutable =
                () -> refreshTokenData.update(
                        "updatedUsername"
                        , updatedDate
                        , List.of("ROLE_PROJECT_MEMBER")
                        , "updatedRefreshTokenString");

        //Execution & Assertions
        assertThrows(IllegalArgumentException.class, refreshTokenDataExecutable);
    }


    @Test
    void checkExpiration_returnsFalse_whenExpired() {
        /*
         * tests if false is returned
         * when refresh token's expiry date is earlier than the given date
         */

        //Setup
        RefreshTokenData refreshTokenData = new RefreshTokenData(
                "testUsername"
                , Date.from(clock.instant())//earlier than the given date
                , List.of("ROLE_TEAM_MEMBER")
                , "refreshTokenString"
        );

        //Execution
        boolean valid = refreshTokenData.checkExpiration(
                clock.instant().plus(1, ChronoUnit.MINUTES));//given date

        //Assertions
        assertFalse(valid);
    }

    @Test
    void checkExpiration_returnsTrue_whenNotExpired() {
        /*
         * tests if true is returned
         * when refresh token's expiry date is later than the given date
         */

        //Setup
        RefreshTokenData refreshTokenData = new RefreshTokenData(
                "testUsername"
                , Date.from(clock.instant()) // later than the given date
                , List.of("ROLE_TEAM_MEMBER")
                , "refreshTokenString"
        );

        //Execution
        boolean valid = refreshTokenData.checkExpiration(
                clock.instant().minus(1, ChronoUnit.MINUTES)); //given date

        //Assertions
        assertTrue(valid);
    }


}
