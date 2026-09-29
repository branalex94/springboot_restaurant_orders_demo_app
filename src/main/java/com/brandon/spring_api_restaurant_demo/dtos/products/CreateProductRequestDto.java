package com.brandon.spring_api_restaurant_demo.dtos.products;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateProductRequestDto(@NotBlank String name,
		@NotNull @DecimalMin(value = "0.01") @Digits(integer = 10, fraction = 2) BigDecimal unitPrice) {

}
