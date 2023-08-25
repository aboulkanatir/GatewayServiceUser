package com.example.demo.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.HttpClientErrorException.Forbidden;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomAuthoriztionFilter  extends OncePerRequestFilter{

	@Override
	protected void doFilterInternal(
			HttpServletRequest request, 
			HttpServletResponse response,
			FilterChain filterChain)
			throws ServletException, IOException {
		
		if (request.getServletPath().equals("/api/login") ||
				request.getServletPath().equals("/api/token/refresh")) {
			filterChain.doFilter(request, response) ; 
		}else {
			String authorizationHeader = request.getHeader("Authorization"); 
		
			if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
				
				try {
					String token = authorizationHeader.substring("Bearer ".length());
					Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
					JWTVerifier verifier = JWT.require(algorithm).build(); 
					DecodedJWT decodeJwt = verifier.verify(token) ;
					String username = decodeJwt.getSubject();
					String[] roles = decodeJwt.getClaim("roles").asArray(String.class) ; 
					Collection<SimpleGrantedAuthority> authorities = new ArrayList<>() ; 
					
					for (int i = 0; i < roles.length; i++) {
						authorities.add(new SimpleGrantedAuthority(roles[i]));
					}
					UsernamePasswordAuthenticationToken authentificationToken = new UsernamePasswordAuthenticationToken(username, null ,  authorities);
				
					SecurityContextHolder.getContext().setAuthentication(authentificationToken) ; 
					filterChain.doFilter(request, response) ; 
					
					
				} catch (Exception e) {
					log.info("Error loggin in : {}"  , e.getMessage()) ; 
					
					response.setHeader("error", e.getMessage()) ; 
					response.setStatus(403) ; 
					
					//response.sendError(403) ; 
					
					Map<String, String> errr = new HashMap<>();
					errr.put("error_message", e.getMessage());
					response.setContentType("application/json");
					new ObjectMapper().writeValue(response.getOutputStream(), errr) ; 
					
				}
			}else {
				filterChain.doFilter(request, response) ; 
			}
		}
		
		
	}

}
