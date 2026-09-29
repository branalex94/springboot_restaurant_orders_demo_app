package com.brandon.spring_api_restaurant_demo.utils;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.brandon.spring_api_restaurant_demo.services.DataInitializerService;

@Configuration
public class DbRolesAndPermissionsSetupInit {

	@Bean
	CommandLineRunner initRolesAndPermissions(
			DataInitializerService dataInitializerService) {
		return args -> dataInitializerService.initialize();
	}
}
