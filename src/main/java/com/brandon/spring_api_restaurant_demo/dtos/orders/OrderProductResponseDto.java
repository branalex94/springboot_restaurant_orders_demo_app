package com.brandon.spring_api_restaurant_demo.dtos.orders;

import java.math.BigDecimal;

public record OrderProductResponseDto(String name, BigDecimal price) {

}
