package com.brandon.spring_api_restaurant_demo.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.brandon.spring_api_restaurant_demo.dtos.ApiResponse;
import com.brandon.spring_api_restaurant_demo.dtos.clients.ClientOrdersResponseDto;
import com.brandon.spring_api_restaurant_demo.dtos.orders.CreateOrderItemDto;
import com.brandon.spring_api_restaurant_demo.dtos.orders.CreateOrderRequestDto;
import com.brandon.spring_api_restaurant_demo.dtos.orders.OrderProductResponseDto;
import com.brandon.spring_api_restaurant_demo.dtos.orders.UpdateOrderStatusRequestDto;
import com.brandon.spring_api_restaurant_demo.dtos.products.OrderResponseDto;
import com.brandon.spring_api_restaurant_demo.enums.OrderStatus;
import com.brandon.spring_api_restaurant_demo.models.Client;
import com.brandon.spring_api_restaurant_demo.models.Order;
import com.brandon.spring_api_restaurant_demo.models.OrderItem;
import com.brandon.spring_api_restaurant_demo.models.OrderStatusHistory;
import com.brandon.spring_api_restaurant_demo.models.Product;
import com.brandon.spring_api_restaurant_demo.repositories.ClientRepository;
import com.brandon.spring_api_restaurant_demo.repositories.OrderRepository;
import com.brandon.spring_api_restaurant_demo.repositories.ProductRepository;

@Service
public class OrderService {

	private final OrderRepository orderRepository;
	private final ClientRepository clientRepository;
	private final ProductRepository productRepository;

	public OrderService(OrderRepository orderRepository,
			ClientRepository clientRepository,
			ProductRepository productRepository) {
		this.orderRepository = orderRepository;
		this.clientRepository = clientRepository;
		this.productRepository = productRepository;
	}

	public ApiResponse<ClientOrdersResponseDto> getClientOrders(Long clientId) {
		List<Order> orders = orderRepository.getOrdersByClientId(clientId);

		List<OrderResponseDto> orderDtos = orders.stream()
				.map(this::buildOrderDto).toList();

		ClientOrdersResponseDto responseDto = new ClientOrdersResponseDto(
				orderDtos);

		return new ApiResponse<>("OK", HttpStatus.OK, responseDto, "");
	}

	public ApiResponse<Void> createOrder(Long clientId,
			CreateOrderRequestDto dto) {
		Client client = clientRepository.findById(clientId).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						"Client not found"));

		Order order = new Order();

		List<Long> productIds = dto.items().stream()
				.map(CreateOrderItemDto::productId).toList();

		List<Product> products = productRepository.findAllById(productIds);

		Map<Long, Product> productsById = products.stream()
				.collect(Collectors.toMap(Product::getId, Function.identity()));

		for (CreateOrderItemDto itemDto : dto.items()) {

			Product product = productsById.get(itemDto.productId());

			if (product == null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND,
						"Product not found");
			}

			OrderItem item = new OrderItem();

			item.setProduct(product);
			item.setQuantity(itemDto.quantity());
			item.setUnitPrice(product.getUnitPrice());

			order.addItem(item);
		}

		order.setClient(client);
		order.setOrderStatus(OrderStatus.PENDING);
		order.setCreatedAt(LocalDateTime.now());
		order.setUpdatedAt(LocalDateTime.now());

		updateOrderStatusHistory(order, order.getOrderStatus());

		orderRepository.save(order);

		return new ApiResponse<Void>("OK", HttpStatus.CREATED, null, "");
	}

	public ApiResponse<?> updateOrderStatus(Long id,
			UpdateOrderStatusRequestDto dto) {
		Order order = findById(id);
		if (dto.orderStatus() != null) {
			order.setOrderStatus(dto.orderStatus());

			updateOrderStatusHistory(order, dto.orderStatus());
		}
		order.setUpdatedAt(LocalDateTime.now());
		orderRepository.save(order);
		return new ApiResponse<Void>("OK", HttpStatus.OK, null, "");
	}

	public ApiResponse<?> cancelOrder(Long id) {
		Order order = findById(id);
		order.setActive(false);
		order.setOrderStatus(OrderStatus.CANCELLED);
		updateOrderStatusHistory(order, OrderStatus.CANCELLED);
		order.setUpdatedAt(LocalDateTime.now());
		orderRepository.save(order);
		return new ApiResponse<Void>("OK", HttpStatus.OK, null, "");
	}

	private void updateOrderStatusHistory(Order order,
			OrderStatus orderStatus) {
		order.addStatusHistory(new OrderStatusHistory(order, orderStatus,
				LocalDateTime.now(), LocalDateTime.now()));
	}

	private Order findById(Long id) {
		return orderRepository.findById(id).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						"Order not found"));
	}

	private OrderResponseDto buildOrderDto(Order order) {

		List<OrderProductResponseDto> products = order.getItems().stream()
				.map(this::buildOrderProductDto).toList();

		BigDecimal total = order.getItems().stream()
				.map(item -> item.getUnitPrice()
						.multiply(BigDecimal.valueOf(item.getQuantity())))
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		return new OrderResponseDto(order.getId(), products, total,
				order.getOrderStatus());
	}

	private OrderProductResponseDto buildOrderProductDto(OrderItem item) {

		Product product = item.getProduct();

		return new OrderProductResponseDto(product.getName(),
				item.getUnitPrice());
	}

}
