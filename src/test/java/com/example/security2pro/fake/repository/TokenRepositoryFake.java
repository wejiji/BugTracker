package com.example.security2pro.fake.repository;

import com.example.security2pro.domain.model.User;
import com.example.security2pro.domain.model.auth.RefreshTokenData;
import com.example.security2pro.repository.repository_interfaces.TokenRepository;
import com.example.security2pro.repository.repository_interfaces.UserRepository;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class TokenRepositoryFake implements TokenRepository {

    private final UserRepository userRepository;

    public TokenRepositoryFake(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    List<RefreshTokenData> refreshTokenDataList
            = new ArrayList<>();

    private Long generatedId = 0L;

    @Override
    public Optional<RefreshTokenData> readRefreshToken(String refreshToken) {
        Optional<RefreshTokenData> foundRefreshTokenDataOptional = refreshTokenDataList.stream()
                .filter(refreshTokenData -> refreshTokenData.getRefreshTokenString().equals(refreshToken))
                .findAny();

        if(foundRefreshTokenDataOptional.isEmpty()) {
            return foundRefreshTokenDataOptional;}

        RefreshTokenData foundRefreshTokenData = foundRefreshTokenDataOptional.get();

        Optional<User> optionalUser = userRepository.findUserByUsername(foundRefreshTokenData.getUser().getUsername());
        if (optionalUser.isEmpty()) {
            throw new NoSuchElementException("test user data should be set up");
        }
        String rolesInString
                = optionalUser.get().getAuthorities()
                .stream()
                .map(Enum::name)
                .collect(Collectors.joining(","));

        foundRefreshTokenData.update(
                optionalUser.get()
                , foundRefreshTokenData.getExpiryDate()
                , foundRefreshTokenData.getRefreshTokenString());

        return Optional.of(foundRefreshTokenData);
    }


    @Override
    public RefreshTokenData createNewToken(RefreshTokenData refreshTokenData) {
        OptionalInt existingRefreshTokenForTheUser
                = IntStream.range(0, refreshTokenDataList.size())
                .filter(i -> refreshTokenDataList.get(i).getUser().getUsername()
                        .equals(refreshTokenData.getUser().getUsername()))
                .findAny();

        if (existingRefreshTokenForTheUser.isEmpty()) {
            generatedId++;
            RefreshTokenData returningRefreshTokenData
                    = new RefreshTokenData(generatedId
                    , refreshTokenData.getUser()
                    , refreshTokenData.getExpiryDate()
                    , refreshTokenData.getRefreshTokenString()
                    );
            refreshTokenDataList.add(returningRefreshTokenData);
            return returningRefreshTokenData;
        }

        RefreshTokenData refreshTokenDataFound = refreshTokenDataList.get(existingRefreshTokenForTheUser.getAsInt());
        refreshTokenDataFound.update(refreshTokenData.getUser()
                , refreshTokenData.getExpiryDate()
                , refreshTokenData.getRefreshTokenString());
        return refreshTokenDataFound;
    }

    @Override
    public void deleteToken(String refreshTokenValue) {
        OptionalInt indexToBeRemoved = IntStream.range(0, refreshTokenDataList.size())
                .filter(i -> refreshTokenDataList.get(i).getRefreshTokenString().equals(refreshTokenValue))
                .findAny();

        if(indexToBeRemoved.isPresent()){
            refreshTokenDataList.remove(indexToBeRemoved.getAsInt());
        }
    }
}
