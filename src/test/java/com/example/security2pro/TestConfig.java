package com.example.security2pro;

import com.example.security2pro.authentication.JwtTokenManagerImplFake;
import com.example.security2pro.repository.*;
import com.example.security2pro.repository.repository_interfaces.*;
import com.example.security2pro.service.authorization.CustomPermissionEvaluator;
import com.example.security2pro.service.authorization.DelegetingPermissionEvaluator;
import com.example.security2pro.service.PasswordEncoderFake;
import com.example.security2pro.service.authentication.JwtTokenManager;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.*;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.TestSecurityContextHolderStrategyAdapter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.time.Clock;
import java.util.List;
import java.util.Locale;
import java.util.Set;


@Profile("test")
@Configuration
//@Import(Scanner.class)
@EnableMethodSecurity
public class TestConfig {
    // Test method security only
    // - authentication setting is permit all in this configuration

    @Bean
    public MethodSecurityExpressionHandler createExpressionHandler(Set<CustomPermissionEvaluator> permissionEvaluatorSet) {
        DefaultMethodSecurityExpressionHandler expressionHandler =
                new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(new DelegetingPermissionEvaluator(permissionEvaluatorSet));
        return expressionHandler;
    }

    @Bean
    public SecurityContextHolderStrategy securityContextHolderStrategy(){
        return new TestSecurityContextHolderStrategyAdapter();
    }


    @Bean
    public Clock clock(){
        return Clock.systemUTC();
    }

    @Bean
    MvcRequestMatcher.Builder mvcMatcher(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager, HandlerMappingIntrospector introspector) throws Exception {


        http.authorizeHttpRequests(auth->auth.requestMatchers(mvcMatcher(introspector).pattern("/create-default-user")
                        ,mvcMatcher(introspector).pattern("/test-preauth/**")).permitAll().anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults());

        //http.authorizeHttpRequests(auth->auth.anyRequest().permitAll());


        http.cors(c-> {
            CorsConfigurationSource source
                    = request->{
                CorsConfiguration config = new CorsConfiguration();
                config.addAllowedOrigin("*");
                config.addAllowedHeader("*");
                config.setAllowedMethods(List.of("GET","POST","PUT","DELETE"));
                return config;
            };
            c.configurationSource(source);
        });

        http.csrf(AbstractHttpConfigurer::disable);



        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new PasswordEncoderFake();
    }

    @Bean
    public UserRepository userRepository(){
        return new UserRepositoryFake();
    }
    @Bean
    public TokenRepository tokenRepository(){
        return new TokenRepositoryFake();
    }
    @Bean
    public SprintRepository sprintRepository(){
        return new SprintRepositoryFake();
    }
    @Bean
    public SprintIssueHistoryRepository sprintIssueHistoryRepository(){
        return new SprintIssueHistoryRepositoryFake();
    }
    @Bean
    public ProjectRepository projectRepository(){
        return new ProjectRepositoryFake();
    }
    @Bean
    public ProjectMemberRepository projectMemberRepository(){
        return new ProjectMemberRepositoryFake();
    }
    @Bean
    public IssueRepository issueRepository(){
        return new IssueRepositoryFake();
    }
    @Bean
    public CommentRepository commentRepository(){
        return new CommentRepositoryFake();
    }
    @Bean
    public JwtTokenManager jwtTokenManager(){
        return new JwtTokenManagerImplFake();
    }


    @Bean
    MessageSource messageSource(){
        //read from errors.properties.
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("errors"); // necessary
        messageSource.setDefaultLocale(Locale.ENGLISH); //wont affect default message setting
        messageSource.setFallbackToSystemLocale(false);
        return messageSource;
    }


}
