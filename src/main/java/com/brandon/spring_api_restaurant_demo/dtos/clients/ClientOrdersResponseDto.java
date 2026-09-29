package com.brandon.spring_api_restaurant_demo.dtos.clients;

import java.util.List;

import com.brandon.spring_api_restaurant_demo.dtos.products.OrderResponseDto;

public record ClientOrdersResponseDto(List<OrderResponseDto> orders) {

}
