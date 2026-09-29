package com.brandon.spring_api_restaurant_demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.brandon.spring_api_restaurant_demo.models.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {

}
