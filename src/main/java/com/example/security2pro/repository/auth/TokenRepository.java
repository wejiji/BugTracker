package com.example.security2pro.repository.auth;


import com.example.security2pro.domain.model.auth.RefreshTokenData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Map;

@Repository
@Slf4j
public class TokenRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    //private RowMapper<RefreshTokenData> refreshMapper = BeanPropertyRowMapper.newInstance(RefreshTokenData.class);

    TokenRepository(DataSource dataSource){
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }


    public RefreshTokenData readRefreshToken(String refreshToken){
        String sql = "select * from refresh_token where refresh_token_string =:refresh_token_string";

        Map<String,String> param = Map.of("refresh_token_string", refreshToken);
        log.info("will try to find refresh token of value from db:" +refreshToken+" ");

        return namedParameterJdbcTemplate.queryForObject(sql, param, BeanPropertyRowMapper.newInstance(RefreshTokenData.class));
    }


    public void createNewToken(RefreshTokenData refreshTokenData){

        String sql ="merge into refresh_token" +
                " using values (:refresh_token_string, :username , :roles, :expiry_date)  rt(refresh_token_string, username, roles, expiry_date)" +
                " on refresh_token.username = rt.username" +
                " when matched then update set refresh_token_string = rt.refresh_token_string, username = rt.username, roles = rt.roles, expiry_date = rt.expiry_date" +
                " when not matched then insert (refresh_token_string, username, roles, expiry_date) values (rt.refresh_token_string, rt.username, rt.roles, rt.expiry_date)";

//        String sql ="insert into refresh_token (refresh_token_string, username, roles, expiry_date) " +
//                " values (:refresh_token_string, :username, :roles, :expiry_date)";


        String rolesString = String.join(",", refreshTokenData.getRoles());

        MapSqlParameterSource param =
                new MapSqlParameterSource(Map.of("refresh_token_string", refreshTokenData.getRefreshTokenString(),
                        "username",refreshTokenData.getUsername(),
                        "roles",rolesString,
                        "expiry_date", refreshTokenData.getExpiryDate())
                ); // 지금에서 maxIndDays더한것

        namedParameterJdbcTemplate.update(sql, param); // 자동키를 쓰도록 DB에 설정할것.
    }

    public void deleteToken(String refreshTokenValue){
        String sql = "delete from refresh_token where refresh_token_string = :refresh_token_string";

        Map<String,String> map = Map.of("refresh_token_string",refreshTokenValue);

        namedParameterJdbcTemplate.update(sql, map);
    }



}
