package com.brandon.spring_api_restaurant_demo.dtos.auth;

public record LoginResponseDto(String accessToken,
		String refreshToken) {

}
