package com.avinashee0012.zorvyn.controller;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.avinashee0012.zorvyn.dto.request.UpdateUserDto;
import com.avinashee0012.zorvyn.dto.request.UserRegisterRequestDto;
import com.avinashee0012.zorvyn.dto.response.UserResponseDto;
import com.avinashee0012.zorvyn.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private static final Set<String> SORT_DIRECTIONS = Set.of("ASC", "DESC");

    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> createUser(
        @RequestBody @Valid UserRegisterRequestDto request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerUser(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    public ResponseEntity<Page<UserResponseDto>> getUsers(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "asc") String sort
    ) {
        size = Math.min(Math.max(size, 1), 50);
        if (!SORT_DIRECTIONS.contains(sort.toUpperCase())) {
            throw new IllegalArgumentException("Invalid sort direction: " + sort);
        }
        return ResponseEntity.status(HttpStatus.OK)
            .body(userService.getPaginatedUsers(page, size, sort));
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> updateUser(
        @PathVariable Long userId,
        @RequestBody @Valid UpdateUserDto request
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUser(userId, request));
    }

    @PutMapping("/{userId}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> toggleUserStatus(@PathVariable Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.toggleUserStatus(userId));
    }

}
