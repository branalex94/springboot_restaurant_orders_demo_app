package com.brandon.spring_api_restaurant_demo.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.brandon.spring_api_restaurant_demo.models.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUsername(String username);
}
