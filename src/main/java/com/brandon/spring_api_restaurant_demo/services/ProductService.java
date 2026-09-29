package com.brandon.spring_api_restaurant_demo.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.brandon.spring_api_restaurant_demo.dtos.ApiResponse;
import com.brandon.spring_api_restaurant_demo.dtos.products.CreateProductRequestDto;
import com.brandon.spring_api_restaurant_demo.dtos.products.ProductResponseDto;
import com.brandon.spring_api_restaurant_demo.dtos.products.UpdateProductRequestDto;
import com.brandon.spring_api_restaurant_demo.models.Product;
import com.brandon.spring_api_restaurant_demo.repositories.ProductRepository;

@Service
public class ProductService {

	private final ProductRepository productRepository;

	public ProductService(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	public ApiResponse<?> createProduct(CreateProductRequestDto dto) {
		LocalDateTime creationDate = LocalDateTime.now();
		Product product = new Product(dto.name(), dto.unitPrice(), creationDate,
				creationDate);
		productRepository.save(product);
		return new ApiResponse<>("OK", HttpStatus.CREATED, null, "");
	}

	public ApiResponse<List<ProductResponseDto>> getProducts() {
		List<Product> products = productRepository.findAll();
		List<ProductResponseDto> productsDtos = products.stream()
				.map(this::buildDtoFromProduct).toList();
		return new ApiResponse<>("OK", HttpStatus.OK, productsDtos, null);
	}

	public ApiResponse<ProductResponseDto> getProductById(Long id) {
		Product product = findById(id);
		return new ApiResponse<>("OK", HttpStatus.OK,
				buildDtoFromProduct(product), null);
	}

	public ApiResponse<?> updateProduct(Long id, UpdateProductRequestDto dto) {
		Product product = findById(id);
		if (dto.name() != null) {
			product.setName(dto.name());
		}
		if (dto.unitPrice() != null) {
			product.setUnitPrice(dto.unitPrice());
		}
		product.setUpdatedAt(LocalDateTime.now());
		return new ApiResponse<>("OK", HttpStatus.OK,
				buildDtoFromProduct(product), null);
	}

	public ApiResponse<?> disableProduct(Long id) {
		Product product = findById(id);
		product.setActive(false);
		product.setUpdatedAt(LocalDateTime.now());
		productRepository.save(product);
		return new ApiResponse<>("OK", HttpStatus.OK,
				buildDtoFromProduct(product), null);
	}

	private Product findById(Long id) {
		return productRepository.findById(id).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						"Product not found"));
	}

	private ProductResponseDto buildDtoFromProduct(Product product) {
		return new ProductResponseDto(product.getId(), product.getName(),
				product.getUnitPrice());
	}
}
