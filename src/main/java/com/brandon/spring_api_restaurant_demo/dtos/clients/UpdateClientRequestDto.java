package com.brandon.spring_api_restaurant_demo.dtos.clients;

import jakarta.validation.constraints.Email;

public record UpdateClientRequestDto(String clientName, String phone,
		@Email String email) {

}
