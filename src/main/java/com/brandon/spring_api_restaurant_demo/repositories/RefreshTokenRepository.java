package com.brandon.spring_api_restaurant_demo.repositories;

import java.util.Optional;
import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.brandon.spring_api_restaurant_demo.models.RefreshToken;

public interface RefreshTokenRepository
		extends JpaRepository<RefreshToken, Long> {

	Optional<RefreshToken> findByTokenHash(String tokenHash);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("""
			update refresh_tokens token
			set token.revoked = true, token.updateddAt = :now
			where token.tokenHash = :hash
			  and token.revoked = false
			  and token.expiresAt > :now
			""")
	int revokeIfActive(@Param("hash") String hash,
			@Param("now") LocalDateTime now);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("""
			update refresh_tokens token
			set token.revoked = true, token.updateddAt = :now
			where token.user.id = :userId and token.revoked = false
			""")
	int revokeAllByUserId(@Param("userId") Long userId,
			@Param("now") LocalDateTime now);
}
