package com.example.bugtracker;

import com.example.bugtracker.domain.model.User;
import com.example.bugtracker.domain.model.auth.SecurityUser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@EnableJpaAuditing
@SpringBootApplication
@ComponentScan
public class BugTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(BugTrackerApplication.class, args);

	}


	@Bean
	public AuditorAware<String> auditorProvider() {


		return ()->Optional.ofNullable(Optional.ofNullable(SecurityContextHolder.getContext())
				.map(SecurityContext::getAuthentication)
				.filter(Authentication::isAuthenticated)
				.map(Authentication::getPrincipal)
				.map(SecurityUser.class::cast)
				.orElseGet(()->new SecurityUser(
						User.createUser(null,"system",null,null,null,null,null,true)))
				.getUsername());
	}

}
