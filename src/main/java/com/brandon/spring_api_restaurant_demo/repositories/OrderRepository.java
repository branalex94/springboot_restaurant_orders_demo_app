package com.brandon.spring_api_restaurant_demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.brandon.spring_api_restaurant_demo.models.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

	public List<Order> getOrdersByClientId(Long clientId);
}
