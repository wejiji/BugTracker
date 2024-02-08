package com.example.bugtracker.config;

import com.example.bugtracker.authentication.exceptionhandler.FilterExceptionHandler;
import com.example.bugtracker.authentication.exceptionhandler.MyAuthenticationEntryPoint;
import com.example.bugtracker.authentication.jwt.JwtAuthenticationFilter;
import com.example.bugtracker.authentication.refresh.RefreshAuthenticationFilter;
import com.example.bugtracker.service.authorization.CustomPermissionEvaluator;
import com.example.bugtracker.service.authorization.DelegetingPermissionEvaluator;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpMethod;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import javax.sql.DataSource;
import java.time.Clock;
import java.util.*;


@Configuration
@EnableWebSecurity
@ComponentScan
@EnableMethodSecurity
@EnableTransactionManagement
@Profile("main")
@EnableJpaRepositories(repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class, basePackages = {"com.example.bugtracker.repository"})
public class AppConfig {

    @Bean
    public Clock clock(){
        return Clock.systemUTC();
    }

    @Bean
    public SecurityContextHolderStrategy securityContextHolderStrategy(){
        return SecurityContextHolder.getContextHolderStrategy();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("com.example.bugtracker");
        factory.setDataSource(dataSource);
        return factory;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory);
        return txManager;
    }

    @Bean
    public MethodSecurityExpressionHandler createExpressionHandler(Set<CustomPermissionEvaluator> permissionEvaluatorSet) {
        DefaultMethodSecurityExpressionHandler expressionHandler =
                new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(new DelegetingPermissionEvaluator(permissionEvaluatorSet));
        return expressionHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(10);
    }


    @Bean
    MvcRequestMatcher.Builder mvc( HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }

    @Bean
    ExceptionHandlerExceptionResolver exceptionResolver(){
        return new ExceptionHandlerExceptionResolver();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager, HandlerMappingIntrospector introspector) throws Exception {

        http
                .cors(c-> {
            CorsConfigurationSource source
                    = request->{
                CorsConfiguration config = new CorsConfiguration();
                config.addAllowedOrigin("*");
                config.addAllowedHeader("*");
                config.setAllowedMethods(List.of("GET","POST","PUT","DELETE"));
                return config;
            };
            c.configurationSource(source);
        })
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth ->
                        auth.requestMatchers(mvc(introspector).pattern(HttpMethod.POST,"/users")
                                       ,mvc(introspector).pattern("/swagger-ui/**")
                                        ,mvc(introspector).pattern("/swagger-resources/**")
                                        ,mvc(introspector).pattern("/v3/api-docs")
                                        ,mvc(introspector).pattern("/v3/api-docs/**")
                                        // to allow access to /v3/api-docs/swagger-config
                                        ,mvc(introspector).pattern(HttpMethod.GET,"/error")
                                )
                                .permitAll()
                                .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .addFilterBefore(new RefreshAuthenticationFilter(authenticationManager), BasicAuthenticationFilter.class)
                .addFilterBefore(new JwtAuthenticationFilter(authenticationManager), RefreshAuthenticationFilter.class)
                .addFilterBefore(new FilterExceptionHandler(), LogoutFilter.class)
                .exceptionHandling(
                        c->c.defaultAuthenticationEntryPointFor(
                                new MyAuthenticationEntryPoint("realm"),mvc(introspector).pattern("/api/login")
                                ));


        return http.build();
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
