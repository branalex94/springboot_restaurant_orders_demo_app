package com.brandon.spring_api_restaurant_demo.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

import com.brandon.spring_api_restaurant_demo.dtos.ApiResponse;
import com.brandon.spring_api_restaurant_demo.dtos.products.CreateProductRequestDto;
import com.brandon.spring_api_restaurant_demo.dtos.products.ProductResponseDto;
import com.brandon.spring_api_restaurant_demo.dtos.products.UpdateProductRequestDto;
import com.brandon.spring_api_restaurant_demo.enums.ActiveStatus;
import com.brandon.spring_api_restaurant_demo.models.Product;
import com.brandon.spring_api_restaurant_demo.repositories.ProductRepository;

class ProductServiceTest {

	private ProductRepository productRepository;
	private ProductService productService;

	@BeforeEach
	void setUp() {
		productRepository = Mockito.mock(ProductRepository.class);
		productService = new ProductService(productRepository);
	}

	@Test
	void newProductsAreActive() {
		productService.createProduct(new CreateProductRequestDto("Soup", new BigDecimal("8.50")));

		ArgumentCaptor<Product> product = ArgumentCaptor.forClass(Product.class);
		verify(productRepository).save(product.capture());
		assertTrue(product.getValue().isActive() == ActiveStatus.ACTIVE.getValue());
	}

	@Test
	void productListExposesActiveStateWithoutFilteringRecords() {
		Product active = product(1L, ActiveStatus.ACTIVE.getValue());
		Product inactive = product(2L, ActiveStatus.INACTIVE.getValue());
		when(productRepository.findAll()).thenReturn(List.of(active, inactive));

		ApiResponse<List<ProductResponseDto>> response = productService.getProducts();

		assertEquals(HttpStatus.OK, response.statusCode());
		assertEquals(2, response.data().size());
		assertTrue(response.data().get(0).active() == ActiveStatus.ACTIVE.getValue());
		assertEquals(false, response.data().get(1).active() == ActiveStatus.INACTIVE.getValue());
	}

	private Product product(Long id, int active) {
		Product product = new Product("Product " + id, new BigDecimal("4.00"), LocalDateTime.now(),
				LocalDateTime.now());
		product.setId(id);
		product.setActive(active);
		return product;
	}

	@Test
	void updatedProductIsChanged() {
		Product product = new Product("Soup", new BigDecimal("8.50"), LocalDateTime.now(), LocalDateTime.now());
		product.setId(1L);
		product.setActive(ActiveStatus.ACTIVE.getValue());
		when(productRepository.findById(1L)).thenReturn(Optional.of(product));
		when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
		String updatedName = "product name altered";
		productService.updateProduct(1L, new UpdateProductRequestDto(updatedName, new BigDecimal("8.50")));
		assertNotEquals("Soup", product.getName());
	}

	@Test
	void updatedProductIsInactivated() {
		Product product = new Product("Soup", new BigDecimal("8.50"), LocalDateTime.now(), LocalDateTime.now());
		product.setId(1L);
		product.setActive(ActiveStatus.ACTIVE.getValue());
		when(productRepository.findById(1L)).thenReturn(Optional.of(product));
		when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
		productService.disableProduct(1L);
		assertTrue(product.isActive() == ActiveStatus.INACTIVE.getValue());
	}
}