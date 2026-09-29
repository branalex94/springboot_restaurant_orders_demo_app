package com.brandon.spring_api_restaurant_demo.dtos.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(@NotBlank String username,
		@NotBlank String password) {

}
