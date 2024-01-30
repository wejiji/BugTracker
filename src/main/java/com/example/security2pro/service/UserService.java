package com.example.security2pro.service;


import com.example.security2pro.domain.model.User;
import com.example.security2pro.dto.user.*;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
public interface UserService extends UserDetailsService {
    void createUser(User user);
    UserResponseDto updateUserNamesAndEmail(UserSimpleUpdateDto userSimpleUpdateDto);
    void deleteUser(String username);
    List<User> findAll();
    Optional<User> findById(Long userId);
    void changePassword(String username, ChangePasswordDto changePasswordDto);
    void updateUser(String username, UserAdminUpdateDto userAdminUpdateDto);
    UserResponseDto register(UserRegistrationDto userRegistrationDto);

}
