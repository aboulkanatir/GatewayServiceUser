package com.example.demo.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.domaine.Role;
import com.example.demo.domaine.User;
import com.example.demo.repo.RoleRepo;
import com.example.demo.repo.UserRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service @RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl  implements UserService , UserDetailsService{

	
	private final UserRepo userRepo ; 
	
	private  final RoleRepo roleRepo ;
	
	private final PasswordEncoder  passwordEncoder ; 
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		User user = userRepo.findByUsername(username) ;
		if (user==null) {
			log.error("user not found in the database");
			throw new  UsernameNotFoundException("user not found in the database");
		}else {
			log.info("user found in the database :{}", username);
		}
		
	Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
	 user.getRoles().forEach(role-> {
		 authorities.add( new SimpleGrantedAuthority(role.getName()) );
		 });
	
		return new org.springframework.security.core.userdetails.User(user.getUsername(),
		user.getPassword(), authorities);
	}
	
	
	@Override
	public User saveUser(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userRepo.save(user);
	}

	@Override
	public Role saveRole(Role role) {
		
		return roleRepo.save(role);
	}

	@Override
	public void addRoleToUser(String username, String roleName) {
		User user = userRepo.findByUsername(username) ; 
		
		Role role =  roleRepo.findByName(roleName); 
		user.getRoles().add(role) ;
		
		//userRepo.save(user) ; 
		
		
	}

	@Override
	public User getUser(String username) {
		return userRepo.findByUsername(username);
	}

	@Override
	public List<User> getUsers() {
		return userRepo.findAll();
	}

	

}
