package com.brandon.spring_api_restaurant_demo.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.brandon.spring_api_restaurant_demo.models.RefreshToken;
import com.brandon.spring_api_restaurant_demo.repositories.RefreshTokenRepository;

class RefreshTokenServiceTest {

	private RefreshTokenRepository refreshTokenRepository;
	private RefreshTokenService refreshTokenService;

	@BeforeEach
	void setUp() {
		refreshTokenRepository = mock(RefreshTokenRepository.class);
		refreshTokenService = new RefreshTokenService(refreshTokenRepository,
				mock(JwtService.class));
	}

	@Test
	void logoutRevokesAnExistingUnrevokedTokenByHash() {
		String rawToken = "refresh-token";
		String tokenHash = refreshTokenService.hashToken(rawToken);
		RefreshToken storedToken = mock(RefreshToken.class);
		when(refreshTokenRepository.findByTokenHash(tokenHash))
				.thenReturn(Optional.of(storedToken));
		when(storedToken.isRevoked()).thenReturn(false);

		refreshTokenService.revokeIfPresent(rawToken);

		verify(storedToken).setRevoked(true);
		verify(refreshTokenRepository).save(storedToken);
	}

	@Test
	void repeatedOrUnknownLogoutIsIdempotent() {
		String rawToken = "refresh-token";
		String tokenHash = refreshTokenService.hashToken(rawToken);
		RefreshToken alreadyRevokedToken = mock(RefreshToken.class);
		when(refreshTokenRepository.findByTokenHash(tokenHash))
				.thenReturn(Optional.of(alreadyRevokedToken));
		when(alreadyRevokedToken.isRevoked()).thenReturn(true);

		assertDoesNotThrow(() -> refreshTokenService.revokeIfPresent(rawToken));
		assertDoesNotThrow(() -> refreshTokenService.revokeIfPresent("unknown"));
		assertDoesNotThrow(() -> refreshTokenService.revokeIfPresent(" "));

		verify(refreshTokenRepository, never()).save(alreadyRevokedToken);
	}
}