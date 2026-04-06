package com.avinashee0012.zorvyn.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.avinashee0012.zorvyn.domain.entity.User;
import com.avinashee0012.zorvyn.domain.enums.Role;
import com.avinashee0012.zorvyn.domain.enums.UserStatus;
import com.avinashee0012.zorvyn.dto.request.UpdateUserDto;
import com.avinashee0012.zorvyn.dto.request.UserLoginRequestDto;
import com.avinashee0012.zorvyn.dto.request.UserRegisterRequestDto;
import com.avinashee0012.zorvyn.dto.response.JwtResponseDto;
import com.avinashee0012.zorvyn.dto.response.UserResponseDto;
import com.avinashee0012.zorvyn.exception.CustomDuplicateEntityException;
import com.avinashee0012.zorvyn.repository.UserRepository;
import com.avinashee0012.zorvyn.security.config.CustomUserDetails;
import com.avinashee0012.zorvyn.security.jwt.JwtService;
import com.avinashee0012.zorvyn.service.UserService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public UserResponseDto registerUser(UserRegisterRequestDto request) {
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new CustomDuplicateEntityException("Email already exists");
        }

        User user = new User(
            request.name().trim().toUpperCase(),
            request.email().trim().toLowerCase(),
            passwordEncoder.encode(request.password())
        );

        user.setRole(Role.VIEWER);

        return toResponse(userRepository.save(user));
    }

    @Override
    public JwtResponseDto authenticateUser(UserLoginRequestDto request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        
        if (principal.getUser().getStatus() != UserStatus.ACTIVE) {
            throw new AuthorizationDeniedException("User account is inactive");
        }
        
        String token = jwtService.generateToken(principal);

        return new JwtResponseDto(
            token,
            "Bearer",
            principal.getUsername(),
            principal.getUser().getRole()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDto> getPaginatedUsers(int page, int size, String sort) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sort)
            ? Sort.Direction.DESC
            : Sort.Direction.ASC;
        PageRequest pageRequest = PageRequest.of(
            Math.max(page, 0),
            Math.min(Math.max(size, 1), 50),
            Sort.by(direction, "name")
        );
        return userRepository.findAll(pageRequest).map(this::toResponse);
    }

    @Override
    public UserResponseDto updateUser(Long userId, UpdateUserDto request) {
        User user = getUser(userId);
        user.updateProfile(request.name(), request.role(), request.status());
        return toResponse(userRepository.save(user));
    }

    @Override
    public UserResponseDto toggleUserStatus(Long userId) {
        User user = getUser(userId);
        user.toggleStatus();
        return toResponse(userRepository.save(user));
    }

    @Override
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
            || !(authentication.getPrincipal() instanceof CustomUserDetails principal)) {
            throw new IllegalArgumentException("Authenticated user not available");
        }
        return principal.getUser().getId();
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }

    private UserResponseDto toResponse(User user) {
        return new UserResponseDto(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole(),
            user.getStatus(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
}
