package com.example.security2pro.domain.model;

import com.example.security2pro.domain.model.auth.RefreshTokenData;

import org.junit.jupiter.api.Test;


import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RefreshTokenDataTest {

    private Clock clock = Clock.fixed(ZonedDateTime.of(
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
    public void testConstructor(){

        RefreshTokenData refreshTokenData = new RefreshTokenData(
                "testUsername"
                , Date.from(clock.instant())
                , List.of("ROLE_PROJECT_MEMBER","ADMIN") // this combination will not exist in actual cases
                , "refreshTokenString"
        );

        assertEquals("refreshTokenString",refreshTokenData.getRefreshTokenString());
        assertEquals("ROLE_PROJECT_MEMBER,ADMIN",refreshTokenData.getRoles());
        assertEquals("testUsername",refreshTokenData.getUsername());
        assertEquals(Date.from(clock.instant()),refreshTokenData.getExpiryDate());

    }

    @Test
    public void testUpdate(){

        RefreshTokenData refreshTokenData = new RefreshTokenData(
                "testUsername"
                , Date.from(clock.instant())
                , List.of("ROLE_TEAM_MEMBER")
                , "refreshTokenString"
        );

        Date updatedDate = Date.from(clock.instant().plus(1, ChronoUnit.DAYS));

        assertEquals("refreshTokenString",refreshTokenData.getRefreshTokenString());
        assertEquals("ROLE_TEAM_MEMBER",refreshTokenData.getRoles());
        assertEquals("testUsername",refreshTokenData.getUsername());
        assertEquals(Date.from(clock.instant()),refreshTokenData.getExpiryDate());

        refreshTokenData.update("updatedUsername"
                ,updatedDate
                ,List.of("ROLE_TEAM_LEAD")
                ,"updatedRefreshTokenString");

        assertEquals("updatedUsername",refreshTokenData.getUsername());
        assertEquals(updatedDate, refreshTokenData.getExpiryDate());
        assertEquals("updatedRefreshTokenString",refreshTokenData.getRefreshTokenString());
        assertEquals("ROLE_TEAM_LEAD",refreshTokenData.getRoles());

    }


}
