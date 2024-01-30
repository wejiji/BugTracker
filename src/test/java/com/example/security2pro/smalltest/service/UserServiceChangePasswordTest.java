package com.example.security2pro.smalltest.service;

import com.example.security2pro.databuilders.UserTestDataBuilder;
import com.example.security2pro.fake.authentication.PasswordEncoderFake;
import com.example.security2pro.service.UserService;
import com.example.security2pro.service.UserServiceImpl;
import com.example.security2pro.fake.authentication.AuthenticationFake;
import com.example.security2pro.fake.authentication.SecurityContextFake;
import com.example.security2pro.fake.authentication.SecurityContextHolderStrategyFake;
import com.example.security2pro.domain.model.User;
import com.example.security2pro.domain.model.auth.SecurityUser;
import com.example.security2pro.dto.user.ChangePasswordDto;
import com.example.security2pro.fake.repository.UserRepositoryFake;
import com.example.security2pro.repository.repository_interfaces.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserServiceChangePasswordTest {

    private final UserRepository userRepository = new UserRepositoryFake();

    private final PasswordEncoder passwordEncoder = new PasswordEncoderFake();

    private SecurityContextHolderStrategy securityContextHolderStrategy

            = new SecurityContextHolderStrategyFake();

    private final UserService userService
            = new UserServiceImpl(
            userRepository
            , passwordEncoder
            , securityContextHolderStrategy);


    @Test
    void changePassword_updatesPassword() {
        /*
         * Verifies if the 'changePassword' method correctly updates 'password' field of a 'User'.
         * This test will check if the 'User' object, the database and 'SecurityContext' are correctly updated.
         *
         * Note that the 'changePassword' method allows the old and new passwords to be the same.
         */

        //Setup
        User userToChangePassword = new UserTestDataBuilder()
                .withUsername("userToChangePassword")
                .withPassword("123#1Gd").build();
        userRepository.save(userToChangePassword);

        SecurityUser securityUser = new SecurityUser(userToChangePassword);

        Authentication authentication
                = new AuthenticationFake(securityUser, true);
        SecurityContext securityContext = new SecurityContextFake();
        securityContext.setAuthentication(authentication);
        securityContextHolderStrategy.setContext(securityContext);

        ChangePasswordDto changePasswordDto
                = new ChangePasswordDto(
                "123#1Gd", "456Nb@*aA");

        //Execution
        userService.changePassword("userToChangePassword", changePasswordDto);

        //Assertions
        User expectedUser = new UserTestDataBuilder()
                .withUsername("userToChangePassword")
                .withPassword("456Nb@*aA").build();

        assertThat(expectedUser)
                .usingRecursiveComparison()
                .isEqualTo(userToChangePassword);

        User userFound = userRepository.findUserByUsername("userToChangePassword").get();
        assertEquals(expectedUser.getPassword(), userFound.getPassword());
        assertEquals(expectedUser.getPassword()
                , securityContextHolderStrategy
                        .getContext()
                        .getAuthentication()
                        .getCredentials());
    }


    @Test
    void changePassword_throwsException_givenWrongOldPassword() {
        /*
         * Verifies if an exception is thrown
         * when the old password value of the 'ChangePasswordDto' argument
         * does not match the old password fetched from the repository
         */

        //Setup
        User userToChangePassword = new UserTestDataBuilder()
                .withUsername("userToChangePassword")
                .withPassword("rightOldPassword").build();
        userToChangePassword = userRepository.save(userToChangePassword);

        SecurityUser securityUser = new SecurityUser(userToChangePassword);

        Authentication authentication
                = new AuthenticationFake(securityUser, true);
        SecurityContext securityContext = new SecurityContextFake();
        securityContext.setAuthentication(authentication);
        securityContextHolderStrategy.setContext(securityContext);

        ChangePasswordDto changePasswordDto
                = new ChangePasswordDto(
                "wrongOldPassword", "456Nb@*aA");

        //Execution
        assertThrows(BadCredentialsException.class
                , () -> userService.changePassword(
                        "userToChangePassword", changePasswordDto));
    }

    @Test
    void testPasswordChangeDto() {
        /*
         * Verifies if 'ChangePasswordDto' is correctly populated from provided arguments.
         */

        //Execution
        ChangePasswordDto changePasswordDto =
                new ChangePasswordDto(
                        "123", "456");
        //Assertions
        assertEquals("456", changePasswordDto.getNewPassword());
        assertEquals("123", changePasswordDto.getOldPassword());
    }


}
