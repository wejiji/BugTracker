package com.example.security2pro.integrationtest;

import com.example.security2pro.databuilders.UserTestDataBuilder;
import com.example.security2pro.domain.enums.UserRole;
import com.example.security2pro.domain.model.User;
import com.example.security2pro.domain.model.auth.RefreshTokenData;
import com.example.security2pro.integrationtest.methodsecurity.TestMethodSecurityController;
import com.example.security2pro.integrationtest.methodsecurity.securitycontextsetter.WithMockCustomUserWithJwt;
import com.example.security2pro.integrationtest.methodsecurity.securitycontextsetter.WithMockCustomUserWithRefreshToken;
import com.example.security2pro.repository.repository_interfaces.TokenRepository;
import com.example.security2pro.repository.repository_interfaces.UserRepository;
import jakarta.servlet.http.Cookie;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TestMethodSecurityController.class)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@ContextConfiguration
@AutoConfigureTestDatabase
class AuthControllerTest {

    /**
     * Note that authentication by spring security filters is bypassed when @WithSecurityContext is used
     * , meaning tests annotated with '@WithMockCustomUserWithJwt' and '@WithMockCustomUserWithRefreshToken'
     * bypass the authentication process, verifies only the controller under test.
     */
    @Autowired
    private WebApplicationContext context;
    private MockMvc mvc;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder= new BCryptPasswordEncoder();

    @Autowired
    TokenRepository tokenRepository;
    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @WithMockCustomUserWithRefreshToken(username = "teamLead")
    void authController_refreshToken_issuesAccess() throws Exception {

        Cookie refreshToken = new Cookie("refresh_token","refreshTokenStringValue");

        mvc.perform(MockMvcRequestBuilders.post("/api/login")
                        .cookie(refreshToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.access_token",Matchers.not(blankOrNullString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username",Matchers.not(blankOrNullString())));
    }

    @Test
    void authController_basicAuth_issuesRefreshAndAccess() throws Exception {
        String encoded = passwordEncoder.encode("teamLeadPassword");
        User user = new UserTestDataBuilder()
                .withUsername("teamLead")
                .withPassword(encoded)
                .withAuthorities(Set.of(UserRole.ROLE_TEAM_LEAD))
                .build();
        user = userRepository.save(user);

        mvc.perform(MockMvcRequestBuilders.post("/api/login")
                .with(httpBasic("teamLead","teamLeadPassword")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(cookie().exists("refresh_token"))
                .andExpect(cookie().httpOnly("refresh_token",true))
                .andExpect(cookie().secure("refresh_token",true))
                .andExpect(cookie().maxAge("refresh_token",3600))
                .andExpect(MockMvcResultMatchers.jsonPath("$.access_token",Matchers.not(blankOrNullString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username",Matchers.not(blankOrNullString())));
    }


    @Test
    @WithMockCustomUserWithRefreshToken(username="teamLead")
    void authController_logoutSuccess_givenRefreshToken() throws Exception {
        String encoded = passwordEncoder.encode("teamLeadPassword");
        User user = new UserTestDataBuilder()
                .withUsername("teamLead")
                .withPassword(encoded)
                .withAuthorities(Set.of(UserRole.ROLE_TEAM_LEAD))
                .build();
        user = userRepository.save(user);

        RefreshTokenData refreshTokenData
                = new RefreshTokenData(
                        1L,
                user,
                Date.from(Instant.now().plus(1, ChronoUnit.DAYS)),
                "refreshTokenStringValue"
        );
        tokenRepository.createNewToken(refreshTokenData);

        assertThat(tokenRepository.readRefreshToken(refreshTokenData.getRefreshTokenString()))
                .isNotEmpty();

        Cookie refreshToken = new Cookie("refresh_token","refreshTokenStringValue");
        mvc.perform(MockMvcRequestBuilders.post("/api/logout")
                .cookie(refreshToken))
                .andDo(print())
                .andExpect(status().isOk());

        assertThat(tokenRepository.readRefreshToken(refreshTokenData.getRefreshTokenString()))
                .isEmpty();
    }

    @Test
   @WithMockCustomUserWithJwt
    void authController_logoutFail_givenNoRefreshToken() throws Exception {
        Cookie randomCookie = new Cookie("random_cookie","randomCookieValue");
        mvc.perform(MockMvcRequestBuilders.post("/api/logout")
                        .cookie(randomCookie))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }




}
