package com.brandon.spring_api_restaurant_demo.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.brandon.spring_api_restaurant_demo.dtos.ApiResponse;
import com.brandon.spring_api_restaurant_demo.dtos.auth.ChangePasswordRequestDto;
import com.brandon.spring_api_restaurant_demo.dtos.users.RegisterUserRequestDto;
import com.brandon.spring_api_restaurant_demo.dtos.users.UpdateUserRequestDto;
import com.brandon.spring_api_restaurant_demo.dtos.users.UserResponseDto;
import com.brandon.spring_api_restaurant_demo.models.Role;
import com.brandon.spring_api_restaurant_demo.models.User;
import com.brandon.spring_api_restaurant_demo.repositories.RoleRepository;
import com.brandon.spring_api_restaurant_demo.repositories.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {

	private final CryptoService cryptoService;
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final JwtService jwtService;

	public UserService(CryptoService cryptoService, UserRepository userRepository, JwtService jwtService,
			RoleRepository roleRepository) {
		this.cryptoService = cryptoService;
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.jwtService = jwtService;
	}

	public ApiResponse<UserResponseDto> createUser(RegisterUserRequestDto dto) {
		try {
			Role userRole = roleRepository.findByName("USER")
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role not found"));
			User newUser = buildUserFromDto(dto);
			newUser.addRole(userRole);
			userRepository.saveAndFlush(newUser);
		} catch (DataIntegrityViolationException e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "No fue posible crear la cuenta");
		}
		return new ApiResponse<UserResponseDto>("OK", HttpStatus.CREATED, null, "");
	}

	public void validateUserValidToCreate(RegisterUserRequestDto dto) {
		Optional<User> findUser = userRepository.findByUsername(dto.username());

		if (findUser.isPresent()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user creation");
		}

	}

	public User buildUserFromDto(RegisterUserRequestDto dto) {
		validateUserValidToCreate(dto);
		LocalDateTime creationDate = LocalDateTime.now();
		String hashedPassword = this.cryptoService.hashPassword(dto.password());
		User newUser = new User(dto.username().trim(), hashedPassword, dto.email().trim(), dto.phone().trim(),
				dto.firstName().trim(), dto.lastName().trim(), creationDate, creationDate, 1);
		System.out.println(newUser.getActive());
		return newUser;
	}

	public String buildUserFullName(User user) {
		return Stream.of(user.getFirstName(), user.getLastName()).filter(Objects::nonNull)
				.filter(name -> !name.isBlank()).collect(Collectors.joining(" "));
	}

	public UserResponseDto buildResponseDtoFromUser(User user) {
		UserResponseDto userDto = new UserResponseDto(user.getId(), user.getUsername(), user.getEmail(),
				user.getFirstName(), user.getLastName(), buildUserFullName(user), user.getCreatedAt(),
				user.getUpdatedAt());
		return userDto;
	}

	public ApiResponse<List<UserResponseDto>> getUsers() {
		List<UserResponseDto> allUsers = userRepository.findAll().stream().filter(u -> u.getActive() == 1)
				.map(u -> buildResponseDtoFromUser(u)).toList();
		ApiResponse<List<UserResponseDto>> usersApiResponse = new ApiResponse<>("OK", HttpStatus.OK, allUsers, "");

		return usersApiResponse;

	}

	public Optional<User> getByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	public ApiResponse<UserResponseDto> getUserById(Long id) {

		User user = findById(id);

		return new ApiResponse<>("OK", HttpStatus.OK, buildResponseDtoFromUser(user), "");
	}

	public User findById(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
	}

	public ApiResponse<Void> updateUser(Long id, UpdateUserRequestDto dto) {
		User user = findById(id);

		LocalDateTime updateTime = LocalDateTime.now();

		if (dto.username() != null) {
			user.setUsername(dto.username());
		}
		if (dto.password() != null) {
			user.setPassword(dto.password());
		}
		if (dto.email() != null) {
			user.setEmail(dto.email());
		}
		if (dto.phone() != null) {
			user.setPhone(dto.phone());
		}
		if (dto.password() != null) {
			user.setPassword(dto.password());
		}
		if (dto.firstName() != null) {
			user.setFirstName(dto.firstName());
		}
		if (dto.lastName() != null) {
			user.setLastName(dto.lastName());
		}

		user.setUpdatedAt(updateTime);
		userRepository.save(user);
		return new ApiResponse<>("OK", HttpStatus.OK, null, "");
	}

	public ApiResponse<Void> removeUser(Long id) {

		changeUserActiveState(id, 0);
		return new ApiResponse<>("OK", HttpStatus.OK, null, "");

	}

	public void changeUserActiveState(Long id, int state) {
		User user = findById(id);

		LocalDateTime updateTime = LocalDateTime.now();

		user.setActive(state);
		user.setUpdatedAt(updateTime);
		userRepository.save(user);
	}

	public User getUserByUsername(String username) {
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
	}

	@Transactional
	public void changePassword(String authorizationToken, ChangePasswordRequestDto dto) {
		String token = authorizationToken.substring(7);
		Boolean isTokenExpired = jwtService.isTokenExpired(token);
		if (isTokenExpired) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid parameters");
		}
		String usernameSubject = jwtService.extractUsername(token);
		User currentUser = userRepository.findByUsername(usernameSubject)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid parameters"));
		Boolean ok = cryptoService.validatePassword(dto.currentPassword(), currentUser.getPassword());
		if (!ok) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid parameters");
		}

		LocalDateTime timestamp = LocalDateTime.now();
		String hashedPassword = cryptoService.hashPassword(dto.newPassword());
		currentUser.setPassword(hashedPassword);
		currentUser.setUpdatedAt(timestamp);
	}

	@Transactional
	public void addRole(Long userId, Long roleId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found"));

		Role role = roleRepository.findById(roleId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role not found"));

		user.addRole(role);
	}
}
