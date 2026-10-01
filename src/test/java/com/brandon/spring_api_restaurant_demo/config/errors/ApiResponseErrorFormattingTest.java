package com.brandon.spring_api_restaurant_demo.config.errors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.brandon.spring_api_restaurant_demo.dtos.ApiFieldError;
import com.brandon.spring_api_restaurant_demo.dtos.ApiResponse;

class ApiResponseErrorFormattingTest {

	@Test
	void validationErrorsReturnBadRequestAndDoNotExposeRejectedValues() {
		ApiResponseErrorFormatting formatter = new ApiResponseErrorFormatting();
		MethodArgumentNotValidException exception = mock(
				MethodArgumentNotValidException.class);
		BindingResult bindingResult = mock(BindingResult.class);
		when(exception.getBindingResult()).thenReturn(bindingResult);
		when(bindingResult.getFieldErrors()).thenReturn(List.of(
				new FieldError("login", "username", "private-value", false,
						null, null, "must not be blank"),
				new FieldError("login", "email", "private@example.test", false,
						null, null, "must be valid")));

		var response = formatter.handleArgumentInvalidException(exception);
		ApiResponse<?> body = response.getBody();

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("VALIDATION_ERROR", body.code());
		assertEquals(List.of(
				new ApiFieldError("email", "must be valid"),
				new ApiFieldError("username", "must not be blank")),
				body.errors());
		assertFalse(body.toString().contains("private-value"));
		assertFalse(body.toString().contains("private@example.test"));
	}

	@Test
	void legacyApiResponseConstructorDefaultsErrorsToAnEmptyList() {
		ApiResponse<Void> response = new ApiResponse<>("OK", HttpStatus.OK, null,
				null);

		assertEquals(List.of(), response.errors());
	}
}