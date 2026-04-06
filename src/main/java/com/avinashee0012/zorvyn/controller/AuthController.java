package com.avinashee0012.zorvyn.controller;

import com.avinashee0012.zorvyn.dto.request.UserLoginRequestDto;
import com.avinashee0012.zorvyn.dto.request.UserRegisterRequestDto;
import com.avinashee0012.zorvyn.dto.response.JwtResponseDto;
import com.avinashee0012.zorvyn.dto.response.UserResponseDto;
import com.avinashee0012.zorvyn.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final UserService userService;

	@PostMapping("/register")
	public ResponseEntity<UserResponseDto> register(
			@RequestBody @Valid UserRegisterRequestDto request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerUser(request));
	}

	@PostMapping("/login")
	public ResponseEntity<JwtResponseDto> login(@RequestBody @Valid UserLoginRequestDto request) {
		return ResponseEntity.status(HttpStatus.OK).body(userService.authenticateUser(request));
	}
}
