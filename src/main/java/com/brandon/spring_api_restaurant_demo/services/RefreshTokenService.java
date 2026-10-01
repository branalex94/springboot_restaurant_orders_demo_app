package com.brandon.spring_api_restaurant_demo.services;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.brandon.spring_api_restaurant_demo.dtos.auth.RefreshTokenResponseDto;
import com.brandon.spring_api_restaurant_demo.models.RefreshToken;
import com.brandon.spring_api_restaurant_demo.models.User;
import com.brandon.spring_api_restaurant_demo.repositories.RefreshTokenRepository;

import jakarta.transaction.Transactional;

@Service
public class RefreshTokenService {

	private final SecureRandom secureRandom = new SecureRandom();
	private final RefreshTokenRepository refreshTokenRepository;

	private final JwtService jwtService;

	public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
			JwtService jwtService) {
		this.refreshTokenRepository = refreshTokenRepository;
		this.jwtService = jwtService;
	}

	@Transactional
	public RefreshTokenResponseDto refresh(String rawRefreshToken) {

		RefreshToken refreshToken = validate(rawRefreshToken);

		User user = refreshToken.getUser();

		revoke(refreshToken);

		String newAccessToken = jwtService.generateToken(user);

		String newRefreshToken = create(user);

		return new RefreshTokenResponseDto(newAccessToken, newRefreshToken);
	}

	public RefreshToken validate(String rawRefresh) {
		String tokenHash = hashToken(rawRefresh);

		RefreshToken refreshToken = refreshTokenRepository
				.findByTokenHash(tokenHash)
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.BAD_REQUEST, "Invalid params"));
		if (refreshToken.isRevoked()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Invalid params");
		}

		if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Invalid params");
		}

		return refreshToken;
	}

	public void revoke(RefreshToken refreshToken) {
		refreshToken.setRevoked(true);
		refreshToken.setUpdateddAt(LocalDateTime.now());
		refreshTokenRepository.save(refreshToken);
	}

	@Transactional
	public void revokeIfPresent(String rawRefreshToken) {
		if (rawRefreshToken == null || rawRefreshToken.isBlank()) {
			return;
		}

		String tokenHash = hashToken(rawRefreshToken);
		Optional<RefreshToken> storedToken = refreshTokenRepository
				.findByTokenHash(tokenHash);
		storedToken.filter(token -> !token.isRevoked()).ifPresent(this::revoke);
	}

	public String create(User user) {
		String rawToken = generateToken();

		String hashToken = hashToken(rawToken);

		LocalDateTime expirationTime = LocalDateTime.now().plus(7,
				ChronoUnit.DAYS);

		LocalDateTime creationDate = LocalDateTime.now();

		RefreshToken refreshToken = new RefreshToken(hashToken, user,
				expirationTime, creationDate, creationDate, false);

		refreshTokenRepository.save(refreshToken);

		return rawToken;
	}

	private String generateToken() {
		byte[] randomBytes = new byte[64];

		secureRandom.nextBytes(randomBytes);

		return Base64.getUrlEncoder().withoutPadding()
				.encodeToString(randomBytes);

	}

	public String hashToken(String token) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");

			byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));

			return HexFormat.of().formatHex(hash);

		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("SHA-256 algorithm not available",
					e);
		}
	}
}
