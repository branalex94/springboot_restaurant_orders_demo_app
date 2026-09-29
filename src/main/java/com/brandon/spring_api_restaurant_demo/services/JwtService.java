package com.brandon.spring_api_restaurant_demo.services;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;

import org.springframework.stereotype.Service;

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

		Date expirationTimeout = new Date(
				System.currentTimeMillis() + 1000 * 60 * 60);
		System.out.println(expirationTimeout);
		return Jwts.builder().subject(user.getUsername()).issuedAt(new Date())
				.expiration(expirationTimeout).signWith(privateKey).compact();
	}

	public String extractUsername(String token) {

		return Jwts.parser().verifyWith(publicKey).build()
				.parseSignedClaims(token).getPayload().getSubject();
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
