package com.brandon.spring_api_restaurant_demo.dtos.orders;

import com.brandon.spring_api_restaurant_demo.enums.OrderStatus;

public record UpdateOrderStatusRequestDto(OrderStatus orderStatus) {

}
