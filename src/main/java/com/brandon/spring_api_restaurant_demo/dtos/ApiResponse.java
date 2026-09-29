package com.brandon.spring_api_restaurant_demo.dtos;

import org.springframework.http.HttpStatus;

public record ApiResponse<T>(String msg, HttpStatus statusCode, T data,
		String code) {

}
