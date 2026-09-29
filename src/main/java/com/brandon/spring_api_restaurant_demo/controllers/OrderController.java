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

import com.brandon.spring_api_restaurant_demo.dtos.orders.CreateOrderRequestDto;
import com.brandon.spring_api_restaurant_demo.dtos.orders.UpdateOrderStatusRequestDto;
import com.brandon.spring_api_restaurant_demo.services.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

	private final OrderService orderService;

	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	@GetMapping("/client/{clientId}")
	public ResponseEntity<?> getClientOrders(@PathVariable Long clientId) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(orderService.getClientOrders(clientId));
	}

	@PostMapping("/client/{clientId}")
	public ResponseEntity<?> createOrder(@PathVariable Long clientId,
			@RequestBody @Valid CreateOrderRequestDto dto) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(orderService.createOrder(clientId, dto));
	}

	@PatchMapping("{id}")
	public ResponseEntity<?> createOrder(@PathVariable Long id,
			@RequestBody @Valid UpdateOrderStatusRequestDto dto) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(orderService.updateOrderStatus(id, dto));
	}

	@DeleteMapping("{id}")
	public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(orderService.cancelOrder(id));
	}
}
