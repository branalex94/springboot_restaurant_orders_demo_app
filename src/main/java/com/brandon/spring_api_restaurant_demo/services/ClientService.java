package com.brandon.spring_api_restaurant_demo.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.brandon.spring_api_restaurant_demo.dtos.ApiResponse;
import com.brandon.spring_api_restaurant_demo.dtos.clients.ClientResponseDto;
import com.brandon.spring_api_restaurant_demo.dtos.clients.CreateClientRequestDto;
import com.brandon.spring_api_restaurant_demo.dtos.clients.UpdateClientRequestDto;
import com.brandon.spring_api_restaurant_demo.models.Client;
import com.brandon.spring_api_restaurant_demo.repositories.ClientRepository;

@Service
public class ClientService {

	private final ClientRepository clientRepository;

	public ClientService(ClientRepository clientRepository) {
		this.clientRepository = clientRepository;
	}

	public ApiResponse<List<ClientResponseDto>> getClients() {
		List<Client> clients = clientRepository.findAll();
		List<ClientResponseDto> clientsDtos = clients.stream()
				.map(this::buildDtoFromClient).toList();
		return new ApiResponse<List<ClientResponseDto>>("OK", HttpStatus.OK,
				clientsDtos, "");
	}

	public ApiResponse<ClientResponseDto> getClientById(Long id) {
		Client client = findById(id);
		return new ApiResponse<ClientResponseDto>("OK", HttpStatus.OK,
				buildDtoFromClient(client), "");
	}

	public ApiResponse<?> createClient(CreateClientRequestDto dto) {
		LocalDateTime creationDate = LocalDateTime.now();
		Client client = new Client(dto.clientName().trim(), dto.email().trim(),
				dto.phone().trim(), creationDate, creationDate);
		client.setActive(true);
		clientRepository.save(client);
		return new ApiResponse<>("OK", HttpStatus.CREATED, null, "");
	}

	public ApiResponse<?> updateClient(Long id, UpdateClientRequestDto dto) {
		Client client = findById(id);
		if (dto.clientName() != null) {
			client.setClientName(dto.clientName());
		}
		if (dto.email() != null) {
			client.setEmail(dto.email());
		}
		if (dto.phone() != null) {
			client.setPhone(dto.phone());
		}
		client.setUpdatedAt(LocalDateTime.now());
		clientRepository.save(client);
		return new ApiResponse<>("OK", HttpStatus.OK, null, "");
	}

	public ApiResponse<?> disableClient(Long id) {
		Client client = findById(id);
		client.setUpdatedAt(LocalDateTime.now());
		client.setActive(false);

		clientRepository.save(client);
		return new ApiResponse<>("OK", HttpStatus.OK, null, "");
	}

	private Client findById(Long id) {
		return clientRepository.findById(id).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						"Client not found"));
	}

	private ClientResponseDto buildDtoFromClient(Client client) {
		return new ClientResponseDto(client.getId(), client.getClientName(),
				client.getPhone(), client.getEmail(), client.isActive());
	}
}
