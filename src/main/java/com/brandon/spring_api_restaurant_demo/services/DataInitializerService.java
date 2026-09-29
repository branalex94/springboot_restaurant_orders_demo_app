package com.brandon.spring_api_restaurant_demo.services;

import org.springframework.stereotype.Service;

import com.brandon.spring_api_restaurant_demo.models.Permission;
import com.brandon.spring_api_restaurant_demo.models.Role;
import com.brandon.spring_api_restaurant_demo.repositories.PermissionRepository;
import com.brandon.spring_api_restaurant_demo.repositories.RoleRepository;

import jakarta.transaction.Transactional;

@Service
public class DataInitializerService {

	private final RoleRepository roleRepository;
	private final PermissionRepository permissionRepository;

	public DataInitializerService(RoleRepository roleRepository,
			PermissionRepository permissionRepository) {
		this.roleRepository = roleRepository;
		this.permissionRepository = permissionRepository;
	}

	@Transactional
	public void initialize() {
		Permission ordersRead = permissionRepository.findByName("orders:read")
				.orElseGet(() -> permissionRepository
						.save(new Permission("orders:read")));

		Permission ordersCreate = permissionRepository
				.findByName("orders:create")
				.orElseGet(() -> permissionRepository
						.save(new Permission("orders:create")));

		Role userRole = roleRepository.findByName("USER")
				.orElseGet(() -> roleRepository.save(new Role("USER")));

		userRole.getPermissions().add(ordersRead);
		userRole.getPermissions().add(ordersCreate);


		roleRepository.save(userRole);
	}
}