package com.brandon.spring_api_restaurant_demo.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.brandon.spring_api_restaurant_demo.dtos.ApiResponse;
import com.brandon.spring_api_restaurant_demo.dtos.auth.LoginRequestDto;
import com.brandon.spring_api_restaurant_demo.dtos.auth.LoginResponseDto;
import com.brandon.spring_api_restaurant_demo.dtos.auth.RefreshTokenRequestDto;
import com.brandon.spring_api_restaurant_demo.dtos.users.RegisterUserRequestDto;
import com.brandon.spring_api_restaurant_demo.models.User;

@Service
public class AuthService {

	private final UserService userService;
	private final CryptoService cryptoService;
	private final JwtService jwtService;
	private final RefreshTokenService refreshTokenService;

	public AuthService(UserService userService, CryptoService cryptoService,
			JwtService jwtService, RefreshTokenService refreshTokenService) {
		this.userService = userService;
		this.cryptoService = cryptoService;
		this.jwtService = jwtService;
		this.refreshTokenService = refreshTokenService;
	}

	public ApiResponse<LoginResponseDto> validateLogin(String username,
			String password) {
		User user = this.userService.getUserByUsername(username);
		boolean userActive = user.getActive();
		if (!userActive) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Invalid credentials");
		}
		boolean pwValidation = this.cryptoService.validatePassword(password,
				user.getPassword());
		if (!pwValidation) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Invalid credentials");
		}
		String token = jwtService.generateToken(user);
		String refreshToken = refreshTokenService.create(user);
		LoginResponseDto tokens = new LoginResponseDto("", token, refreshToken);
		return new ApiResponse<LoginResponseDto>("OK", HttpStatus.OK, tokens,
				password);
	}

	public ApiResponse<?> register(RegisterUserRequestDto dto) {
		return this.userService.createUser(dto);
	}

	public ApiResponse<?> login(LoginRequestDto dto) {
		return validateLogin(dto.username(), dto.password());
	}

	public ApiResponse<?> logout() {
		return new ApiResponse<>("OK", HttpStatus.OK, null, "");
	}

	public ApiResponse<?> refreshToken(RefreshTokenRequestDto dto) {
		return new ApiResponse<>("OK", HttpStatus.OK,
				refreshTokenService.refresh(dto.refreshToken()), "");
	}
}
