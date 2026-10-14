package com.brandon.spring_api_restaurant_demo.services;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.brandon.spring_api_restaurant_demo.models.Role;
import com.brandon.spring_api_restaurant_demo.models.User;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

@Service
public class JwtService {
	private final PrivateKey privateKey;
	private final PublicKey publicKey;

	public JwtService(PrivateKey privateKey, PublicKey publicKey) {
		this.privateKey = privateKey;
		this.publicKey = publicKey;
	}

	public String generateToken(User user) {

		// 60 minutos de expiracion
		Date expirationTimeout = new Date(System.currentTimeMillis() + 1000 * 60 * 60);
		List<String> roles = user.getRoles().stream().map(Role::getName).toList();
		System.out.println(roles);
		return Jwts.builder().subject(user.getUsername()).issuedAt(new Date()).expiration(expirationTimeout)
				.claim("roles", roles)
				.signWith(privateKey).compact();
	}

	public String extractUsername(String token) {

		return Jwts.parser().verifyWith(publicKey).build().parseSignedClaims(token).getPayload().getSubject();
	}

	public boolean isTokenExpired(String token) {
		try {
			Date expiration = Jwts.parser().verifyWith(publicKey).build().parseSignedClaims(token).getPayload()
					.getExpiration();

			return expiration.before(new Date());

		} catch (RuntimeException e) {
			return true;
		}
	}

	public boolean isTokenValid(String token) {

		try {
			extractUsername(token);
			return true;
		} catch (JwtException | IllegalArgumentException ex) {
			return false;
		}
	}
}
