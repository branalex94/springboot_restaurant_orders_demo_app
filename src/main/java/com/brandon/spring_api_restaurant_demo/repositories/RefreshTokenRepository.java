package com.brandon.spring_api_restaurant_demo.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.brandon.spring_api_restaurant_demo.models.RefreshToken;

public interface RefreshTokenRepository
		extends JpaRepository<RefreshToken, Long> {

	Optional<RefreshToken> findByTokenHash(String tokenHash);
}
