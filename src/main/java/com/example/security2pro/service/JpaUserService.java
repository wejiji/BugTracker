package com.example.security2pro.service;




import com.example.security2pro.domain.enums.refactoring.UserRole;
import com.example.security2pro.domain.model.auth.SecurityUser;
import com.example.security2pro.domain.model.User;
import com.example.security2pro.dto.user.*;
import com.example.security2pro.repository.repository_interfaces.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class JpaUserService implements UserService {
    //Manager 말고 Service 를 써야할까??? 오버라이딩에 캐스트 하기 힘들다

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final SecurityContextHolderStrategy securityContextHolderStrategy;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow( ()-> new UsernameNotFoundException("username was not found"));
        return new SecurityUser(user);
    }

    public Optional<User> findById(Long userId){ //the message should not be used for authentication or login
        return userRepository.findById(userId);
    }

    public User getReferenceById(Long userId){
        return userRepository.getReferenceById(userId);
    }


    public void createUser(User user) {//각 필드의 bind validation은 이미 시행 되었음. 여기서는 DB관련한 validation 해야함. 중복 체크 -
        //처음 가입시 role 설정해주기. verification은 따로
        userRepository.save(user);
    }

    public UserResponseDto register(UserRegistrationDto userRegistrationDto){
        String username = userRegistrationDto.getUsername();

        Optional<User> userOptional = userRepository.findUserByUsername(username);
        if(userOptional.isPresent()){
            throw new DuplicateKeyException("user with username " +username+" already exist");
        }

        String password= passwordEncoder.encode(userRegistrationDto.getPassword());
        String firstName = userRegistrationDto.getFirstName();
        String lastName = userRegistrationDto.getLastName();
        String email = userRegistrationDto.getEmail();

        User user = User.createUser(null,username,password,firstName,lastName,email,null,false);

        return new UserResponseDto(userRepository.save(user));
    }

    public UserResponseDto updateUserNamesAndEmail(UserSimpleUpdateDto userSimpleUpdateDto) {
        Long userId = userSimpleUpdateDto.getId();

        User user = userRepository.getReferenceById(userId);

        String firstName = userSimpleUpdateDto.getFirstName();
        String lastName= userSimpleUpdateDto.getLastName();
        String email = userSimpleUpdateDto.getEmail();

        user.updateNamesAndEmail(firstName,lastName,email);
        User userSaved = userRepository.save(user);
        return new UserResponseDto(userSaved);
    }

    public void updateUser(String username, UserAdminUpdateDto userAdminUpdateDto){

        Optional<User> userOptional = userRepository.findUserByUsername(username);
        if(userOptional.isEmpty()){
            throw new IllegalArgumentException("user with username " +username+" does not exist");
        }

        String updatedUsername = userAdminUpdateDto.getUsername();
        String password = passwordEncoder.encode(userAdminUpdateDto.getPassword());
        String firstName = userAdminUpdateDto.getFirstName();
        String lastName = userAdminUpdateDto.getLastName();
        String email = userAdminUpdateDto.getEmail();
        Set<UserRole> authorities = userAdminUpdateDto.getRoles();
        boolean enabled = userAdminUpdateDto.isEnabled();
        User user = userOptional.get();
        user.adminUpdate(updatedUsername,password,firstName,lastName,email,authorities,enabled);
        userRepository.save(user);
    }


    public void deleteUser(String username) {
        if((userRepository.findUserByUsername(username).isEmpty())){
            throw new IllegalArgumentException("username does not exist");
        }
        userRepository.deleteByUsername(username);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void changePassword(ChangePasswordDto changePasswordDto) {

        Authentication currentUser = this.securityContextHolderStrategy.getContext().getAuthentication();
        String username = currentUser.getName();

        Optional<User> userOptional= userRepository.findUserByUsername(username);
        if(userOptional.isEmpty()){
            throw new IllegalArgumentException("username "+ username + "does not exist");
        }
        String existingPasswordFound= userOptional.get().getPassword();
        if(!passwordEncoder.matches(changePasswordDto.getOldPassword(),existingPasswordFound)){
            throw new IllegalArgumentException("incorrect old password for the logged in user");
        }

       //updateDB
        Optional<User> user = userRepository.findUserByUsername(username);
        String newPassword = passwordEncoder.encode(changePasswordDto.getNewPassword());
        user.get().changePassword(newPassword);
        userRepository.save(user.get());

        //update security context
        Authentication authentication = createNewAuthentication(currentUser, newPassword);
        SecurityContext context = this.securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authentication);
        this.securityContextHolderStrategy.setContext(context);
    }


    protected Authentication createNewAuthentication(Authentication currentAuth, String newPassword) {
        UserDetails user = loadUserByUsername(currentAuth.getName());
        UsernamePasswordAuthenticationToken newAuthentication = UsernamePasswordAuthenticationToken.authenticated(user,
                newPassword, user.getAuthorities());
        newAuthentication.setDetails(currentAuth.getDetails());
        return newAuthentication;
    }




}
