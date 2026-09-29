package com.brandon.spring_api_restaurant_demo.dtos.users;

import jakarta.validation.constraints.Email;

public record RegisterUserRequestDto(String username, String password,
		@Email String email, String phone, String firstName, String lastName) {

}
