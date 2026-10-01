package com.brandon.spring_api_restaurant_demo.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.brandon.spring_api_restaurant_demo.dtos.ApiResponse;
import com.brandon.spring_api_restaurant_demo.dtos.auth.LoginResponseDto;
import com.brandon.spring_api_restaurant_demo.dtos.auth.RefreshTokenRequestDto;
import com.brandon.spring_api_restaurant_demo.models.User;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private UserService userService;

	@Mock
	private CryptoService cryptoService;

	@Mock
	private JwtService jwtService;

	@Mock
	private RefreshTokenService refreshTokenService;

	@InjectMocks
	private AuthService authService;

	@Test
	void successfulLoginDoesNotReturnTheSubmittedPassword() {
		String password = "correct-horse-battery";
		User user = org.mockito.Mockito.mock(User.class);
		when(userService.getUserByUsername("manager")).thenReturn(user);
		when(user.getActive()).thenReturn(true);
		when(cryptoService.validatePassword(password, null)).thenReturn(true);
		when(jwtService.generateToken(user)).thenReturn("access-token");
		when(refreshTokenService.create(user)).thenReturn("refresh-token");

		ApiResponse<LoginResponseDto> response = authService.validateLogin(
				"manager", password);

		assertEquals(HttpStatus.OK, response.statusCode());
		assertNull(response.code());
		assertEquals("access-token", response.data().accessToken());
		assertEquals("refresh-token", response.data().refreshToken());
	}

	@Test
	void logoutRequestsRefreshTokenRevocation() {
		RefreshTokenRequestDto request = new RefreshTokenRequestDto(
				"refresh-token");

		ApiResponse<?> response = authService.logout(request);

		verify(refreshTokenService).revokeIfPresent("refresh-token");
		assertEquals(HttpStatus.OK, response.statusCode());
		assertNull(response.data());
	}
}