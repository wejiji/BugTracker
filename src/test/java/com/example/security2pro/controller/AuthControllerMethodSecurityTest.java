package com.example.security2pro.controller;

import com.example.security2pro.databuilders.ProjectMemberTestDataBuilder;
import com.example.security2pro.databuilders.ProjectTestDataBuilder;
import com.example.security2pro.databuilders.UserTestDataBuilder;

import com.example.security2pro.domain.enums.refactoring.ProjectMemberRole;
import com.example.security2pro.domain.model.Project;
import com.example.security2pro.domain.model.ProjectMember;
import com.example.security2pro.domain.model.User;
import com.example.security2pro.domain.model.auth.RefreshTokenData;
import com.example.security2pro.repository.repository_interfaces.ProjectMemberRepository;
import com.example.security2pro.repository.repository_interfaces.ProjectRepository;
import com.example.security2pro.repository.repository_interfaces.TokenRepository;
import com.example.security2pro.repository.repository_interfaces.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Set;


import static com.example.security2pro.authorization.ProjectMemberPermissionEvaluatorTest.projectId;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@ContextConfiguration
@ActiveProfiles("test")
public class AuthControllerMethodSecurityTest {

    //Authorization test


    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;


    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Clock clock = Clock.fixed(ZonedDateTime.of(
            2030,
            1,
            1,
            1,
            10,
            10,
            1,
            ZoneId.systemDefault()).toInstant(),
            ZoneId.systemDefault());


    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        RefreshTokenData refreshTokenData = new RefreshTokenData("admin", Date.from(clock.instant()), List.of("ROLE_ADMIN"),"refreshTokenStringValue");
        tokenRepository.createNewToken(refreshTokenData);

        Project project = new ProjectTestDataBuilder().withId(Long.valueOf(projectId)).build();

        projectRepository.save(project);

        User adminUser = new UserTestDataBuilder().withUsername("yj").withPassword(passwordEncoder.encode("1235")).build();
        User projectMemberUser = new UserTestDataBuilder().withId(10L).withUsername("projectMember").build();
        User projectLeadUser = new UserTestDataBuilder().withId(20L).withUsername("projectLead").build();


        userRepository.save(adminUser);
        userRepository.save(projectMemberUser);
        userRepository.save(projectLeadUser);


        ProjectMember projectMember =
                new ProjectMemberTestDataBuilder().withId(43L).withProject(project).withUser(projectMemberUser).withAuthorities(Set.of(ProjectMemberRole.ROLE_PROJECT_MEMBER)).build();


        ProjectMember projectLead =
                new ProjectMemberTestDataBuilder().withId(22L).withProject(project).withUser(projectLeadUser).withAuthorities(Set.of(ProjectMemberRole.ROLE_PROJECT_LEAD)).build();

        projectMemberRepository.save(projectMember);
        projectMemberRepository.save(projectLead);
    }


    @Test
    void test_BasicAuth() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/api/login")
                        .header(HttpHeaders.AUTHORIZATION,
                                "Basic "+ Base64.getEncoder().encodeToString("yj:1235".getBytes()) ))
                .andExpect(status().isOk());

    }

    @Test
    @WithMockCustomUserWithRefreshToken
        //This sets SecurityContext with RefreshTokenAuthentication
    void test_RefreshAuth() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/api/login")
                        )
                .andExpect(status().isOk());
    }

    @Test
    @WithMockCustomUserWithJwt(username = "projectMember")
    void test_Jwt_Denied() throws Exception {
        //member role is not allowed to access
        mvc.perform(MockMvcRequestBuilders.get("/test-preauth/"+projectId))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUserWithJwt
    void test_Jwt_Ok() throws Exception {
        //member role is not allowed to access
        mvc.perform(MockMvcRequestBuilders.get("/test-preauth/"+String.valueOf(10L)))
                .andExpect(status().isOk());
    }



}
