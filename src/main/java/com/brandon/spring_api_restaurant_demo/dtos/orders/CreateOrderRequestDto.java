package com.brandon.spring_api_restaurant_demo.dtos.orders;

import java.util.List;

public record CreateOrderRequestDto(List<CreateOrderItemDto> items) {

}
