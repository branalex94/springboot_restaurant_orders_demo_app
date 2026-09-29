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

import com.brandon.spring_api_restaurant_demo.dtos.clients.CreateClientRequestDto;
import com.brandon.spring_api_restaurant_demo.dtos.clients.UpdateClientRequestDto;
import com.brandon.spring_api_restaurant_demo.services.ClientService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

	private final ClientService clientService;

	public ClientController(ClientService clientService) {
		this.clientService = clientService;
	}

	@GetMapping("")
	public ResponseEntity<?> getClients() {
		return ResponseEntity.status(HttpStatus.OK)
				.body(clientService.getClients());
	}

	@GetMapping("{id}")
	public ResponseEntity<?> getClientById(@PathVariable Long id) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(clientService.getClientById(id));
	}

	@PostMapping("")
	public ResponseEntity<?> createClient(
			@RequestBody @Valid CreateClientRequestDto dto) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(clientService.createClient(dto));
	}

	@PatchMapping("{id}")
	public ResponseEntity<?> updateClient(@PathVariable Long id,
			@RequestBody @Valid UpdateClientRequestDto dto) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(clientService.updateClient(id, dto));
	}

	@DeleteMapping("{id}")
	public ResponseEntity<?> removeClient(@PathVariable Long id) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(clientService.disableClient(id));
	}

}
