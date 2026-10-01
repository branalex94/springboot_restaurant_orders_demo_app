package com.brandon.spring_api_restaurant_demo.dtos;

import org.springframework.http.HttpStatus;

import java.util.List;

public record ApiResponse<T>(String msg, HttpStatus statusCode, T data,
		String code, List<ApiFieldError> errors) {

	public ApiResponse(String msg, HttpStatus statusCode, T data, String code) {
		this(msg, statusCode, data, code, List.of());
	}

	public ApiResponse {
		errors = errors == null ? List.of() : List.copyOf(errors);
	}

}
