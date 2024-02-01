package com.example.bugtracker.service;




import com.example.bugtracker.domain.enums.UserRole;
import com.example.bugtracker.exception.directmessageconcretes.NotExistException;
import com.example.bugtracker.domain.model.auth.SecurityUser;
import com.example.bugtracker.domain.model.User;
import com.example.bugtracker.dto.user.*;
import com.example.bugtracker.repository.repository_interfaces.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    /*
     * While calling the 'save' method is sometimes unnecessary when the entity is already in the cache,
     * as dirty checking can automatically update modified fields, it is called nevertheless for explicitness.
    */

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final SecurityContextHolderStrategy securityContextHolderStrategy;

    public void createUser(User user) {
        // This method will de deleted - just for testing
        userRepository.save(user);
    }

     /**
     * Load a user by username, fetching the authorities along with the user.
     * An overridden method of the 'UserDetailService' interface
     * to be used by Spring Security's 'DaoAuthenticationProvider' to load a user.
     * Note that the global fetch type for 'authorities' of 'User' is FetchType.Lazy,
     * and 'DaoAuthenticationProvider' accesses the authorities of 'User'
     * during the authentication process, requiring them to be initialized.
     *
     * @param username The username of the user to be loaded.
     * @return UserDetails representing the loaded user.
     * @throws UsernameNotFoundException If the user with the provided username is not found.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.loadUserByUsernameWithAuthorities(username)
                .orElseThrow( ()-> new UsernameNotFoundException("username was not found"));
        return new SecurityUser(user);
    }

    public Optional<User> findById(Long userId){
        return userRepository.findById(userId);
    }

    /**
     * Registers a new user by creating and saving a 'User' object.
     * Note that the 'enabled' field is set to false when a user is first created.
     *
     * @param userRegistrationDto The data required for user registration.
     * @return A 'UserResponseDto' with the auto-generated user id.
     * @throws DuplicateKeyException If a user with the same username already exists.
     */
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

        User user = User.createUser(
                null,username,password,firstName,lastName,email,null,false);
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


    /**
     * Updates user data, including the password, typically by an admin user.
     * Note that the 'username' field can be only updated in the database.
     *
     * @param username              The username of the user to be updated.
     * @param userAdminUpdateDto    DTO containing the updated user data.
     * @throws NotExistException    If the user with the specified username does not exist.
     */
    public void updateUser(String username, UserAdminUpdateDto userAdminUpdateDto){
        Optional<User> userOptional = userRepository.findUserByUsername(username);
        if(userOptional.isEmpty()){
            throw new NotExistException("user with username " + username + " does not exist");
        }

        String password = passwordEncoder.encode(userAdminUpdateDto.getPassword());
        String firstName = userAdminUpdateDto.getFirstName();
        String lastName = userAdminUpdateDto.getLastName();
        String email = userAdminUpdateDto.getEmail();
        Set<UserRole> authorities = userAdminUpdateDto.getRoles();
        boolean enabled = userAdminUpdateDto.isEnabled();
        User user = userOptional.get();
        user.adminUpdate(
                password
                ,firstName
                ,lastName
                ,email
                ,authorities
                ,enabled);
        userRepository.save(user);
    }


    public void deleteUser(String username) {
        userRepository.deleteByUsername(username);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }


    /**
     * Updates the password of a user, typically used when the user changes their own password.
     *
     * @param username              The username of the user, expected to exist and be a member of the project.
     * @param changePasswordDto     Data containing the old and new passwords.
     * @throws BadCredentialsException If the provided old password doesn't match the existing password.
     */
    public void changePassword(String username, ChangePasswordDto changePasswordDto) {
        User user= userRepository.findUserByUsername(username).get();

        String existingPasswordFound= user.getPassword();
        if(!passwordEncoder.matches(changePasswordDto.getOldPassword(),existingPasswordFound)){
            throw new BadCredentialsException("incorrect old password for the logged in user");
        }

       //Updates the database
        String newPassword = passwordEncoder.encode(changePasswordDto.getNewPassword());
        user.changePassword(newPassword);
        userRepository.save(user);

        //Updates the 'SecurityContext'
        Authentication authentication = createNewAuthentication(user, newPassword);
        SecurityContext context = this.securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authentication);
        this.securityContextHolderStrategy.setContext(context);
    }

    protected Authentication createNewAuthentication(User user, String newPassword) {
        SecurityUser securityUser = new SecurityUser(user);
        return UsernamePasswordAuthenticationToken.authenticated(securityUser,
                newPassword, user.getAuthorities().stream()
                        .map(auth->new SimpleGrantedAuthority(auth.name()))
                        .collect(Collectors.toCollection(HashSet::new)));
    }




}
