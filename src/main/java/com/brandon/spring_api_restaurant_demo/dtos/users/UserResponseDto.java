package com.brandon.spring_api_restaurant_demo.dtos.users;

import java.time.LocalDateTime;

public record UserResponseDto(Long id, String username, String email,
		String firstName, String lastName, String fullName,
		LocalDateTime createdAt, LocalDateTime updatedAt) {

}
