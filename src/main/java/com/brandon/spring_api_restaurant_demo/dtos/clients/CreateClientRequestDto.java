package com.brandon.spring_api_restaurant_demo.dtos.clients;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateClientRequestDto(@NotBlank String clientName,
		@NotBlank String phone, @NotBlank @Email String email) {

}
