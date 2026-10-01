package com.brandon.spring_api_restaurant_demo.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

import com.brandon.spring_api_restaurant_demo.dtos.ApiResponse;
import com.brandon.spring_api_restaurant_demo.dtos.clients.ClientResponseDto;
import com.brandon.spring_api_restaurant_demo.dtos.clients.CreateClientRequestDto;
import com.brandon.spring_api_restaurant_demo.models.Client;
import com.brandon.spring_api_restaurant_demo.repositories.ClientRepository;

class ClientServiceTest {

	private ClientRepository clientRepository;
	private ClientService clientService;

	@BeforeEach
	void setUp() {
		clientRepository = Mockito.mock(ClientRepository.class);
		clientService = new ClientService(clientRepository);
	}

	@Test
	void newClientsAreActive() {
		clientService.createClient(new CreateClientRequestDto(" Ana ",
				"555-0100", "ana@example.test"));

		ArgumentCaptor<Client> client = ArgumentCaptor.forClass(Client.class);
		verify(clientRepository).save(client.capture());
		assertTrue(client.getValue().isActive());
	}

	@Test
	void clientListExposesContactFieldsAndActiveState() {
		Client active = client(1L, true);
		Client inactive = client(2L, false);
		when(clientRepository.findAll()).thenReturn(List.of(active, inactive));

		ApiResponse<List<ClientResponseDto>> response = clientService.getClients();

		assertEquals(HttpStatus.OK, response.statusCode());
		assertEquals(2, response.data().size());
		ClientResponseDto dto = response.data().get(0);
		assertEquals("555-0100", dto.phone());
		assertEquals("ana@example.test", dto.email());
		assertTrue(dto.active());
		assertEquals(false, response.data().get(1).active());
	}

	private Client client(Long id, boolean active) {
		Client client = new Client("Ana", "ana@example.test", "555-0100",
				LocalDateTime.now(), LocalDateTime.now());
		client.setId(id);
		client.setActive(active);
		return client;
	}
}