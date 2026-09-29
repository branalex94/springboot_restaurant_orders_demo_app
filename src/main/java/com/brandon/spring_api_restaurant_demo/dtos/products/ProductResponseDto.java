package com.brandon.spring_api_restaurant_demo.dtos.products;

import java.math.BigDecimal;

public record ProductResponseDto(Long id, String name, BigDecimal unitPrice) {

}
