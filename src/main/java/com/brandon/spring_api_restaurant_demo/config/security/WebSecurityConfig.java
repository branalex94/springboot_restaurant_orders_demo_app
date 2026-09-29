package com.brandon.spring_api_restaurant_demo.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity(debug = true)
public class WebSecurityConfig {

	private final JwtAuthFilter jwtFilter;

	public WebSecurityConfig(JwtAuthFilter jwtFilter) {
		this.jwtFilter = jwtFilter;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http)
			throws Exception {
		http.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests((requests) -> requests
						// .requestMatchers(HttpMethod.OPTIONS,
						// "/**").permitAll()
						// .requestMatchers("/**").permitAll()
						.requestMatchers("/api/auth/**").permitAll()
//				.requestMatchers("/api/users/**").permitAll()
						.requestMatchers("/error").permitAll()
						.requestMatchers("/api/**").authenticated()

				// .anyRequest().authenticated()
				// se
				// elimino
				// hasta
				// entender
				// como
				// funciona
				// el
				// auth
				).httpBasic(Customizer.withDefaults())
				.csrf((csrf) -> csrf.ignoringRequestMatchers("/**"))
				.addFilterBefore(jwtFilter,
						UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}
}
