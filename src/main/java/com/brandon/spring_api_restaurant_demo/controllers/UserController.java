package com.brandon.spring_api_restaurant_demo.controllers;

import org.apache.catalina.realm.AuthenticatedUserRealm;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brandon.spring_api_restaurant_demo.dtos.auth.ChangePasswordRequestDto;
import com.brandon.spring_api_restaurant_demo.dtos.users.RegisterUserRequestDto;
import com.brandon.spring_api_restaurant_demo.dtos.users.UpdateUserRequestDto;
import com.brandon.spring_api_restaurant_demo.services.UserService;

import io.jsonwebtoken.Jwts;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping
	public ResponseEntity<?> getUsers() {
		return ResponseEntity.ok(this.userService.getUsers());
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getUserById(@PathVariable Long id) {
		return ResponseEntity.ok(this.userService.getUserById(id));
	}

	@PostMapping
	public ResponseEntity<?> createUser(
			@Valid @RequestBody RegisterUserRequestDto dto) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(this.userService.createUser(dto));
	}

	@PatchMapping("/{id}")
	public ResponseEntity<?> updateUser(@PathVariable Long id,
			@Valid @RequestBody UpdateUserRequestDto dto) {
		return ResponseEntity.ok(this.userService.updateUser(id, dto));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> removeUser(@PathVariable Long id) {
		return ResponseEntity.ok(this.userService.removeUser(id));
	}
	
	@PostMapping("/password/me")
	public ResponseEntity<?> changePassword(@RequestHeader("Authorization") String authorization,
                                   @Valid @RequestBody ChangePasswordRequestDto dto) {
     userService.changePassword(authorization, dto);
     return ResponseEntity.noContent().build();
   }
}

//@RestController
//@RequestMapping("/api/users")
//public class UserController {
//
//  @GetMapping("/me")
//  public ResponseEntity<?> me(@AuthenticationPrincipal AuthenticatedUser me) {
//    return ResponseEntity.ok(userService.getUserById(me.userId()));
//  }
//
//  @PatchMapping("/me")   // solo nombre y apellido
//  public ResponseEntity<?> updateProfile(@AuthenticationPrincipal AuthenticatedUser me,
//                                         @Valid @RequestBody UpdateProfileRequestDto dto) {
//    return ResponseEntity.ok(userService.updateProfile(me.userId(), dto));
//  }
//
//  @PostMapping("/me/password")
//  public ResponseEntity<?> changePassword(@AuthenticationPrincipal AuthenticatedUser me,
//                                          @Valid @RequestBody ChangePasswordRequestDto dto) {
//    userService.changePassword(me.userId(), dto.currentPassword(), dto.newPassword());
//    return ResponseEntity.noContent().build();
//  }
//
//  @DeleteMapping("/me")
//  public ResponseEntity<?> deleteMe(@AuthenticationPrincipal AuthenticatedUser me) {
//    userService.removeUser(me.userId());
//    return ResponseEntity.noContent().build();
//  }
//}

//@RestController
//@RequestMapping("/api/admin/users")
//@PreAuthorize("hasAnyRole('MODERATOR','ADMIN')")
//class AdminUserController { /* listado paginado, consultar estado, sanciones */ }