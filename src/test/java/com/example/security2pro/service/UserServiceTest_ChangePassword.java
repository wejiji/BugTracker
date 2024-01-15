package com.example.security2pro.service;

import com.example.security2pro.databuilders.UserTestDataBuilder;
import com.example.security2pro.domain.model.User;
import com.example.security2pro.domain.model.auth.SecurityUser;
import com.example.security2pro.dto.user.ChangePasswordDto;
import com.example.security2pro.repository.UserRepositoryFake;
import com.example.security2pro.repository.repository_interfaces.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserServiceTest_ChangePassword {

    private final UserRepository userRepository= new UserRepositoryFake();

    private final PasswordEncoder passwordEncoder = new PasswordEncoderFake();

    private SecurityContextHolderStrategy securityContextHolderStrategy

        = new SecurityContextHolderStrategyFake(); //just holds authentication

    private final UserService userService = new JpaUserService(userRepository,passwordEncoder,securityContextHolderStrategy);


    @Test
    public void changePassword(){
        User userToChangePassword = new UserTestDataBuilder().withPassword("123#1Gd").build();
        userRepository.save(userToChangePassword);

        SecurityUser securityUser = new SecurityUser(userToChangePassword);

        Authentication authentication = new AuthenticationFake(securityUser,true);
        SecurityContext securityContext = new SecurityContextFake();
        securityContext.setAuthentication(authentication);
        securityContextHolderStrategy.setContext(securityContext);

        ChangePasswordDto changePasswordDto = new ChangePasswordDto("123#1Gd","456Nb@*aA");

        //Execution
        userService.changePassword(changePasswordDto);

        User expectedUser = new UserTestDataBuilder().withPassword("456Nb@*aA").build();
        assertThat(expectedUser).usingRecursiveComparison().isEqualTo(userToChangePassword);
    }

    @Test
    public void testPasswordChangeDto() {
        ChangePasswordDto changePasswordDto = new ChangePasswordDto("123", "456");
        assertEquals("456", changePasswordDto.getNewPassword());
        assertEquals("123", changePasswordDto.getOldPassword());
    }





}
