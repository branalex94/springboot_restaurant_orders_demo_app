package com.brandon.spring_api_restaurant_demo.services;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class CryptoService {

	public String hashPassword(String password) {
		String salt = BCrypt.gensalt();
		return BCrypt.hashpw(password, salt);
	}

	public boolean validatePassword(String incomingPassword,
			String savedPassword) {
		return BCrypt.checkpw(incomingPassword, savedPassword);
	}
}
