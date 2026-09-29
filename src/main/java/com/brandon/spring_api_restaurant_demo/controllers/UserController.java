package com.brandon.spring_api_restaurant_demo.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brandon.spring_api_restaurant_demo.dtos.users.RegisterUserRequestDto;
import com.brandon.spring_api_restaurant_demo.dtos.users.UpdateUserRequestDto;
import com.brandon.spring_api_restaurant_demo.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("")
	public ResponseEntity<?> getUsers() {
		return ResponseEntity.status(HttpStatus.OK)
				.body(this.userService.getUsers());
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getUserById(@PathVariable Long id) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(this.userService.getUserById(id));
	}

	@PostMapping("")
	public ResponseEntity<?> createUser(
			@Valid @RequestBody RegisterUserRequestDto dto) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(this.userService.createUser(dto));
	}

	@PatchMapping("/{id}")
	public ResponseEntity<?> udpdateUser(@PathVariable Long id,
			@Valid @RequestBody UpdateUserRequestDto dto) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(this.userService.updateUser(id, dto));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> removeUser(@PathVariable Long id) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(this.userService.removeUser(id));
	}
}
