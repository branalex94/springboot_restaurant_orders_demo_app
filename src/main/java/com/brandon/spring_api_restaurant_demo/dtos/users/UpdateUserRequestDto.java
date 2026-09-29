package com.brandon.spring_api_restaurant_demo.dtos.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateUserRequestDto(@NotBlank String username,
		@NotBlank String password, @NotBlank @Email String email,
		@NotBlank String phone, @NotBlank String firstName,
		@NotBlank String lastName) {

}
