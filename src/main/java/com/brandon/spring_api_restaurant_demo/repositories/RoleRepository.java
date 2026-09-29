package com.brandon.spring_api_restaurant_demo.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.brandon.spring_api_restaurant_demo.models.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

	Optional<Role> findByName(String name);
}
