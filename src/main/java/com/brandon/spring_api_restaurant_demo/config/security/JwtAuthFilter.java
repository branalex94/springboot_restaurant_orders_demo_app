package com.brandon.spring_api_restaurant_demo.config.security;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.brandon.spring_api_restaurant_demo.services.JwtService;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

	private final JwtService jwtService;

	public JwtAuthFilter(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String authorization = request.getHeader("Authorization");

		if (authorization == null || !authorization.startsWith("Bearer ")) {

			filterChain.doFilter(request, response);
			return;
		}

		String token = authorization.substring(7);

		try {

			String username = jwtService.extractUsername(token);

			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
					username, null, Collections.emptyList());

			SecurityContextHolder.getContext()
					.setAuthentication(authentication);

		} catch (JwtException ex) {

			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

			return;
		}

		filterChain.doFilter(request, response);
	}
}
