package com.brandon.spring_api_restaurant_demo.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brandon.spring_api_restaurant_demo.dtos.auth.LoginRequestDto;
import com.brandon.spring_api_restaurant_demo.dtos.auth.RefreshTokenRequestDto;
import com.brandon.spring_api_restaurant_demo.dtos.users.RegisterUserRequestDto;
import com.brandon.spring_api_restaurant_demo.services.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("register")
	public ResponseEntity<?> register(
			@Valid @RequestBody RegisterUserRequestDto dto) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(this.authService.register(dto));
	}

	@PostMapping("login")
	public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto dto) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(authService.login(dto));
	}

	@PostMapping("logout")
	public ResponseEntity<?> logout(
			@Valid @RequestBody RefreshTokenRequestDto dto) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(authService.logout(dto));
	}

	@PostMapping("account-recovery")
	public ResponseEntity<?> recoverAccount() {
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}

	@PostMapping("refresh")
	public ResponseEntity<?> refreshToken(
			@RequestBody @Valid RefreshTokenRequestDto dto) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(authService.refreshToken(dto));
	}
}
