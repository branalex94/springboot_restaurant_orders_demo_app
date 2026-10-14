package com.brandon.spring_api_restaurant_demo.config.security;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.brandon.spring_api_restaurant_demo.models.Role;
import com.brandon.spring_api_restaurant_demo.models.User;
import com.brandon.spring_api_restaurant_demo.services.JwtService;
import com.brandon.spring_api_restaurant_demo.services.UserService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final UserService userService;

	public JwtAuthFilter(JwtService jwtService, UserService userService) {
		this.jwtService = jwtService;
		this.userService = userService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
			FilterChain chain)
			throws ServletException, IOException {

		String header = req.getHeader(HttpHeaders.AUTHORIZATION);
		if (header != null && header.startsWith("Bearer ")) {
			try {
				String token = header.substring(7).trim();
				if (token.isEmpty() || !jwtService.isTokenValid(token)) {
					SecurityContextHolder.clearContext();
				} else {
					String username = jwtService.extractUsername(token);
					userService.getByUsername(username)
							.filter(user -> Boolean.TRUE.equals(user.getActive()))
							.ifPresentOrElse(this::authenticate, SecurityContextHolder::clearContext);
				}
			} catch (JwtException | IllegalArgumentException ex) {
				SecurityContextHolder.clearContext();
			}
		}
		chain.doFilter(req, res);
	}

	private void authenticate(User user) {
		List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
				.map(Role::getName)
				.map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
				.map(SimpleGrantedAuthority::new)
				.toList();

		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken(user.getUsername(), null,
						authorities));
	}
}
