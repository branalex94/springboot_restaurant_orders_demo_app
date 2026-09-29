package com.brandon.spring_api_restaurant_demo.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.brandon.spring_api_restaurant_demo.models.Permission;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

	Optional<Permission> findByName(String name);
}
