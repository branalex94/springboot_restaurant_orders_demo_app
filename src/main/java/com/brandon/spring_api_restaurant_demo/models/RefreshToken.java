package com.brandon.spring_api_restaurant_demo.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Table
@Entity(name = "refresh_tokens")
public class RefreshToken {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String tokenHash;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private User user;

	@Column(nullable = false)
	private LocalDateTime expiresAt;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime updateddAt;

	@Column(nullable = false)
	private boolean revoked;

	public RefreshToken(String tokenHash, User user, LocalDateTime expiresAt,
			LocalDateTime createdAt, LocalDateTime updateddAt,
			boolean revoked) {
		super();
		this.tokenHash = tokenHash;
		this.user = user;
		this.expiresAt = expiresAt;
		this.createdAt = createdAt;
		this.updateddAt = updateddAt;
		this.revoked = revoked;
	}

	public RefreshToken() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTokenHash() {
		return tokenHash;
	}

	public void setTokenHash(String tokenHash) {
		this.tokenHash = tokenHash;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public LocalDateTime getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(LocalDateTime expiresAt) {
		this.expiresAt = expiresAt;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdateddAt() {
		return updateddAt;
	}

	public void setUpdateddAt(LocalDateTime updateddAt) {
		this.updateddAt = updateddAt;
	}

	public boolean isRevoked() {
		return revoked;
	}

	public void setRevoked(boolean revoked) {
		this.revoked = revoked;
	}

}
