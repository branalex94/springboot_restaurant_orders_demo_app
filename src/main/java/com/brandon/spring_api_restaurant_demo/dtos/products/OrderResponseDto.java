package com.brandon.spring_api_restaurant_demo.dtos.products;

import java.math.BigDecimal;
import java.util.List;

import com.brandon.spring_api_restaurant_demo.dtos.orders.OrderProductResponseDto;
import com.brandon.spring_api_restaurant_demo.enums.OrderStatus;

public record OrderResponseDto(Long id, List<OrderProductResponseDto> products,
		BigDecimal totalPrice, OrderStatus orderStatus) {

}
