package com.example.security2pro.service;



import com.example.security2pro.domain.model.auth.SecurityUser;
import com.example.security2pro.domain.model.User;
import com.example.security2pro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class JpaUserService implements UserService {
    //Manager 말고 Service 를 써야할까??? 오버라이딩에 캐스트 하기 힘들다

    private final UserRepository userRepository;

    private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder
            .getContextHolderStrategy();

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

    public void updateUser(User user) {
        userRepository.save(user);
    }

    public void deleteUser(String username) {
        userRepository.deleteByUsername(username);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void changePassword(String oldPassword, String newPassword) {
        Authentication currentUser = this.securityContextHolderStrategy.getContext().getAuthentication();
        String username = currentUser.getName();
        //reauthentication - authentication manager에 authentication을 줄경우 unauthenticated token 넘어오면 다시 인증 시도함
        // circular dependency발생 .
        // authenticationManager가 여기오기전에 미리 reauthenticate 했다고 가정해야 함.

//        this.authenticationManager
//                .authenticate(UsernamePasswordAuthenticationToken.unauthenticated(username, oldPassword));

        //validateNewPassword(newPassword); //여기서는 password 의 binding validation룰은 이미 앞쪽에서 만족한뒤에 changePassword 메서드로 들어온다고 생각해야 함.
        //updateDB
        Optional<User> user = userRepository.findUserByUsername(username);
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
                null, user.getAuthorities());
        newAuthentication.setDetails(currentAuth.getDetails());
        return newAuthentication;
    }

    public boolean userExists(String username) {
        Optional<User> user = userRepository.findUserByUsername(username);
        if(user.isEmpty()){
            return false;
        }
        return true;
    }

}
