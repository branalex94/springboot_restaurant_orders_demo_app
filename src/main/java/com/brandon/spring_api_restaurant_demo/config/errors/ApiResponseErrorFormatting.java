package com.brandon.spring_api_restaurant_demo.config.errors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import com.brandon.spring_api_restaurant_demo.dtos.ApiResponse;
import com.brandon.spring_api_restaurant_demo.dtos.ApiFieldError;

import java.util.Comparator;
import java.util.List;

@RestControllerAdvice
public class ApiResponseErrorFormatting {

	private static final Log log = LogFactory
			.getLog(ApiResponseErrorFormatting.class);

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ApiResponse<Void>> handleResponseStatusException(
			ResponseStatusException ex) {

		log.error("Unexpected error", ex);

		HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());

		ApiResponse<Void> response = new ApiResponse<>(ex.getReason(),
				HttpStatus.valueOf(ex.getStatusCode().value()), null, "ERROR");

		return ResponseEntity.status(status).body(response);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handleGenericException(
			Exception ex) {

		ApiResponse<Void> response = new ApiResponse<>("Internal server error",
				HttpStatus.INTERNAL_SERVER_ERROR, null, "INTERNAL_ERROR");

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(response);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Void>> handleArgumentInvalidException(
			MethodArgumentNotValidException ex) {

		List<ApiFieldError> errors = ex.getBindingResult().getFieldErrors()
				.stream()
				.sorted(Comparator.comparing(error -> error.getField()))
				.map(error -> new ApiFieldError(error.getField(),
						error.getDefaultMessage() == null ? "Invalid value"
								: error.getDefaultMessage()))
				.toList();

		ApiResponse<Void> response = new ApiResponse<>("Validation failed",
				HttpStatus.BAD_REQUEST, null, "VALIDATION_ERROR", errors);

		return ResponseEntity.badRequest().body(response);
	}
}
