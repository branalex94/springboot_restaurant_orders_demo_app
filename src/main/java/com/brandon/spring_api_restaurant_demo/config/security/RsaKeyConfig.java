package com.brandon.spring_api_restaurant_demo.config.security;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class RsaKeyConfig {
	@Value("${security.jwt.private-key-location}")
	private Resource privateKeyResource;

	@Value("${security.jwt.public-key-location}")
	private Resource publicKeyResource;

	@Bean
	public PrivateKey privateKey() throws Exception {
		String key = new String(
				privateKeyResource.getInputStream().readAllBytes(),
				StandardCharsets.UTF_8);

		key = key.replace("-----BEGIN PRIVATE KEY-----", "")
				.replace("-----END PRIVATE KEY-----", "").replaceAll("\\s", "");

		byte[] decoded = Base64.getDecoder().decode(key);

		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);

		KeyFactory factory = KeyFactory.getInstance("RSA");

		return factory.generatePrivate(spec);
	}

	@Bean
	public PublicKey publicKey() throws Exception {
		String key = new String(
				publicKeyResource.getInputStream().readAllBytes(),
				StandardCharsets.UTF_8);

		key = key.replace("-----BEGIN PUBLIC KEY-----", "")
				.replace("-----END PUBLIC KEY-----", "").replaceAll("\\s", "");

		byte[] decoded = Base64.getDecoder().decode(key);

		X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);

		KeyFactory factory = KeyFactory.getInstance("RSA");

		return factory.generatePublic(spec);
	}
}
