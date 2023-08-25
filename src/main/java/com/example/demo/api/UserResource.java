package com.example.demo.api;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.domaine.Role;
import com.example.demo.domaine.User;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserResource {
	
	
	private final UserService userService ; 
	
	
	@GetMapping("/users")
	public ResponseEntity<List<User>> getUsers(){
		
		return ResponseEntity.ok(userService.getUsers()) ; 
	}
	
	@PostMapping("/users/save")
	public ResponseEntity<User> saveUser(@RequestBody User user){
		
		URI uri =  URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user/save").toUriString()) ;
		
		return ResponseEntity.created(uri).body(userService.saveUser(user)) ; 
	}
	
	@PostMapping("/role/save")
	public ResponseEntity<Role> saveRole(@RequestBody Role role){
		
		URI uri =  URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/role/save").toUriString()) ;
		
		return ResponseEntity.created(uri).body(userService.saveRole(role)) ; 
	}
	
	
	@PostMapping("/role/addtoUser")
	public ResponseEntity<?> addroletouser(@RequestBody RoleToUserForm form){
		
		userService.addRoleToUser(form.getUsername() ,
				form.getRoleName())	; 
		
		return ResponseEntity.ok().build() ; 
	}
	
	
	@GetMapping("/token/refresh")
	public void  refrechToken(
			HttpServletRequest request,
			HttpServletResponse response) throws IOException{
		
		String authorizationHeader = request.getHeader("Authorization"); 
		
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			
			try {
				String refrechtoken = authorizationHeader.substring("Bearer ".length());
				Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
				JWTVerifier verifier = JWT.require(algorithm).build(); 
				DecodedJWT decodeJwt = verifier.verify(refrechtoken) ;
				String username = decodeJwt.getSubject();
				
				User user = userService.getUser(username);
				
				String access_token = JWT.create()
						.withSubject(user.getUsername())
						.withExpiresAt(new Date(System.currentTimeMillis() + 10*60*1000))
						.withIssuer(request.getRequestURI().toString())
						.withClaim("roles", user.getRoles().stream()
								.map(Role::getName).collect(Collectors.toList()))
								.sign(algorithm) ; 
				String refresh_token = JWT.create()
						.withSubject(user.getUsername())
						.withExpiresAt(new Date(System.currentTimeMillis() +30*60*1000))
						.withIssuer(request.getRequestURI().toString())
								.sign(algorithm) ; 
				
			/*response.setHeader("access_token", access_token);
			response.setHeader("refresh_token", refresh_token);*/
			Map<String, String> token = new HashMap<>();
			token.put("access_token", access_token);
			token.put("refresh_token", refresh_token);
			response.setContentType("application/json");
			new ObjectMapper().writeValue(response.getOutputStream(), token) ; 
				
			} catch (Exception e) {
			//	log.info("Error loggin in : {}"  , e.getMessage()) ; 
				
				response.setHeader("error", e.getMessage()) ; 
				response.setStatus(403) ; 
				
				//response.sendError(403) ; 
				
				Map<String, String> errr = new HashMap<>();
				errr.put("error_message", e.getMessage());
				response.setContentType("application/json");
				new ObjectMapper().writeValue(response.getOutputStream(), errr) ; 
				
			}
		}else {
			throw new  RuntimeException("Ref rech  token is missing ");
		}
		
	
	}
	
	

}

@Data
class RoleToUserForm{
	private String username ;
	private String roleName  ;
}




















