package com.example.security2pro.repository;

import com.example.security2pro.domain.model.auth.RefreshTokenData;
import com.example.security2pro.repository.repository_interfaces.TokenRepository;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.*;
import java.util.stream.IntStream;


public class TokenRepositoryFake implements TokenRepository {

    List<RefreshTokenData> refreshTokenDataList
            = new ArrayList<>();

    @Override
    public RefreshTokenData readRefreshToken(String refreshToken) {
        return refreshTokenDataList.stream()
                .filter(refreshTokenData -> refreshTokenData.getRefreshTokenString().equals(refreshToken))
                .findAny()
                .orElseThrow(()-> new EmptyResultDataAccessException(1));
    }

    @Override
    public void createNewToken(RefreshTokenData refreshTokenData) {
        OptionalInt existingRefreshTokenForTheUser
                = IntStream.range(0,refreshTokenDataList.size())
                        .filter(i->refreshTokenDataList.get(i).getUsername().equals(refreshTokenData.getUsername()))
                                .findAny();

        if(existingRefreshTokenForTheUser.isEmpty()){
            refreshTokenDataList.add(refreshTokenData);
            return;
        }

        RefreshTokenData refreshTokenDataFound= refreshTokenDataList.get(existingRefreshTokenForTheUser.getAsInt());
        refreshTokenDataFound.update(refreshTokenData.getUsername()
                ,refreshTokenData.getExpiryDate()
                , Arrays.stream(refreshTokenData.getRoles().split(",")).toList()
                ,refreshTokenData.getRefreshTokenString());

    }

    @Override
    public void deleteToken(String refreshTokenValue) {
        Integer indexToBeRemoved = IntStream.range(0,refreshTokenDataList.size())
                .filter(i->refreshTokenDataList.get(i).getRefreshTokenString().equals(refreshTokenValue))
                        .findAny().getAsInt();

        refreshTokenDataList.remove(indexToBeRemoved);
    }
}
