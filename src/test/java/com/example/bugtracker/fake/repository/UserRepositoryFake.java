package com.example.bugtracker.fake.repository;

import com.example.bugtracker.databuilders.UserTestDataBuilder;
import com.example.bugtracker.domain.model.User;
import com.example.bugtracker.repository.repository_interfaces.UserRepository;
import jakarta.persistence.EntityNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.IntStream;

public class UserRepositoryFake implements UserRepository {

    private List<User> userList = new ArrayList<>();

    private Long generatedId = 0L;

    @Override
    public Optional<User> findUserByUsername(String username) {
        return userList.stream().filter(user-> user.getUsername().equals(username)).findAny();
    }

    @Override
    public Optional<User> loadUserByUsernameWithAuthorities(String username) {
        return  userList.stream().filter(user-> user.getUsername().equals(username)).findAny();
    }

    @Override
    public Optional<User> findById(Long userId) {
        return userList.stream().filter(user->user.getId().equals(userId)).findAny();
    }

    @Override
    public User getReferenceById(Long userId) {
        return userList.stream().filter(user->user.getId().equals(userId)).findAny()
                .orElseThrow(()->new EntityNotFoundException("user with id "+userId+" not found"));
    }

    @Override
    public User save(User newUser) {
        if(newUser.getId()==null){
            generatedId++;
            User user = new UserTestDataBuilder()
                    .withId(generatedId)
                    .withUsername(newUser.getUsername())
                    .withPassword(newUser.getPassword())
                    .withFirstName(newUser.getFirstName())
                    .withLastName(newUser.getLastName())
                    .withEmail(newUser.getEmail())
                    .withEnabled(newUser.isEnabled())
                    .withAuthorities(newUser.getAuthorities())
                    .build();

            userList.add(user);
            return user;
        }

        OptionalInt foundUserIndex = IntStream.range(0,userList.size())
                .filter(i->newUser.getId().equals(userList.get(i).getId()))
                .findFirst();

        if(foundUserIndex.isPresent()){
            userList.remove(foundUserIndex.getAsInt());
        }
        userList.add(newUser);
        return newUser;
    }

    @Override
    public void deleteByUsername(String username) {
        OptionalInt foundUserIndex = IntStream.range(0,userList.size())
                .filter(i->username.equals(userList.get(i).getUsername()))
                .findFirst();
        userList.remove(foundUserIndex.getAsInt());

    }

    @Override
    public List<User> findAll() {
        return userList;
    }
}
