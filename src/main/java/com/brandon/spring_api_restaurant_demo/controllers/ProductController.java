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

import com.brandon.spring_api_restaurant_demo.dtos.products.CreateProductRequestDto;
import com.brandon.spring_api_restaurant_demo.dtos.products.UpdateProductRequestDto;
import com.brandon.spring_api_restaurant_demo.services.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
public class ProductController {

	private final ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@GetMapping
	public ResponseEntity<?> getProducts() {
		return ResponseEntity.status(HttpStatus.OK)
				.body(productService.getProducts());
	}

	@GetMapping("{id}")
	public ResponseEntity<?> getProductById(@PathVariable Long id) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(productService.getProductById(id));
	}

	@PostMapping
	public ResponseEntity<?> createProduct(
			@RequestBody @Valid CreateProductRequestDto dto) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(productService.createProduct(dto));
	}

	@PatchMapping("{id}")
	public ResponseEntity<?> updateProduct(@PathVariable Long id,
			@RequestBody @Valid UpdateProductRequestDto dto) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(productService.updateProduct(id, dto));
	}

	@DeleteMapping("{id}")
	public ResponseEntity<?> removeProduct(@PathVariable Long id) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(productService.disableProduct(id));
	}
}
