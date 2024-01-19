package com.example.security2pro.config;


import com.example.security2pro.service.CustomPermissionEvaluator;
import com.example.security2pro.service.DelegetingPermissionEvaluator;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
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
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;


import javax.sql.DataSource;
import java.time.Clock;
import java.util.*;

//@Configuration
@EnableWebSecurity
@ComponentScan
@EnableMethodSecurity
@EnableTransactionManagement
@EnableJpaRepositories(repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class, basePackages = {"com.example.security2pro.repository"})
public class SecurityConfig {

    @Bean
    public Clock clock(){
        return Clock.systemUTC();
    }

    @Bean
    public SecurityContextHolderStrategy securityContextHolderStrategy(){
        return SecurityContextHolder.getContextHolderStrategy();
    }


    @Bean
    public DataSource dataSource(){
//        HikariDataSource dataSource = new HikariDataSource();
//        dataSource.setDriverClassName("org.h2.Driver");
//        dataSource.setJdbcUrl("jdbc:h2:tcp://localhost/~/todo2");
//        dataSource.setUsername("sa");
//        dataSource.setPassword("");
//        dataSource.setMaximumPoolSize(10);
//        dataSource.setPoolName("MyPool");

//        Properties properties = getadditionalJpaProperties();
//        HikariDataSource dataSource = (HikariDataSource) DataSourceBuilder.create()
//                .driverClassName("org.h2.Driver").url("jdbc:h2:tcp://localhost/~/todo2").username("sa")
//                .password("").type(HikariDataSource.class).build();
//        dataSource.setMaximumPoolSize(10);
//        return dataSource;

        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
        driverManagerDataSource.setDriverClassName("org.h2.Driver");
        driverManagerDataSource.setUrl("jdbc:h2:tcp://localhost/~/todo2");
        driverManagerDataSource.setUsername("sa");
        driverManagerDataSource.setConnectionProperties(getadditionalJpaProperties());
        driverManagerDataSource.setPassword("");
        return driverManagerDataSource;
    }

    Properties getadditionalJpaProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", "create");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        properties.setProperty("hibernate.show_sql", "true");

        return properties;
    }


    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("com.example.security2pro");
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
    SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager, HandlerMappingIntrospector introspector) throws Exception {

 //       http.authorizeHttpRequests(auth->auth.anyRequest().permitAll());

        http.authorizeHttpRequests(auth->auth.requestMatchers(mvc(introspector).pattern("/create-default-user")).permitAll().anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults());


//        http.authorizeHttpRequests(auth ->
//                auth.requestMatchers(mvc(introspector).pattern("/api/register/users")
//                                ,mvc(introspector).pattern("/create-default-user")
//                                ,mvc(introspector).pattern("/test-preauth/**"))
//                        .permitAll()
//                        .anyRequest().authenticated())
//                .httpBasic(Customizer.withDefaults())
//                .addFilterBefore(new RefreshAuthenticationFilter(authenticationManager), BasicAuthenticationFilter.class)
//                .addFilterBefore(new JwtAuthenticationFilter(authenticationManager), RefreshAuthenticationFilter.class)
//                .exceptionHandling(c->c.authenticationEntryPoint(new MyAuthenticationEntryPoint("realm"))
//                        .defaultAuthenticationEntryPointFor
//                                (new MyAuthenticationEntryPoint("realm"),mvc(introspector).pattern("/api/login"))
//                );



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
    MessageSource messageSource(){
        //read from errors.properties.
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("errors"); // necessary
        messageSource.setDefaultLocale(Locale.ENGLISH); //wont affect default message setting
        messageSource.setFallbackToSystemLocale(false);
        return messageSource;
    }


}
