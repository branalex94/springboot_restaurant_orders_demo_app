package com.brandon.spring_api_restaurant_demo.dtos.auth;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequestDto(@NotBlank String currentPassword, @NotBlank String newPassword) {

}
