package com.example.demo;

import java.util.ArrayList;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.domaine.Role;
import com.example.demo.domaine.User;
import com.example.demo.service.UserService;

@SpringBootApplication
public class UserserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserserviceApplication.class, args);
	}
	
	@Bean
	PasswordEncoder PasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	CommandLineRunner run(UserService userService) {
		
		return args->{
			userService.saveRole(new Role(null , "ROLE_USER")) .equals(userService);
			userService.saveRole(new Role(null , "ROLE_MANAGER")) .equals(userService);
			userService.saveRole(new Role(null , "ROLE_ADMIN")) .equals(userService);
			userService.saveRole(new Role(null , "ROLE_SUPER_ADMIN")) .equals(userService);
			
			
			userService.saveUser(new User(null , "John tracolta", "jhon","1234" , new ArrayList<>()));
			userService.saveUser(new User(null , "med kanatir", "med","1234" , new ArrayList<>()));
			userService.saveUser(new User(null , "sami sakota", "sami","1234" , new ArrayList<>()));
			userService.saveUser(new User(null , "mohammed", "mohammed","1234" , new ArrayList<>()));
			
			userService.addRoleToUser("jhon", "ROLE_USER");
			userService.addRoleToUser("med", "ROLE_ADMIN");
			userService.addRoleToUser("sami", "ROLE_ADMIN");
			userService.addRoleToUser("mohammed", "ROLE_SUPER_ADMIN");
			
		};
	}

}
