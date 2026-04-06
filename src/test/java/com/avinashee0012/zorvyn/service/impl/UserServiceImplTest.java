package com.avinashee0012.zorvyn.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

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

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserServiceImpl userService;

    @AfterEach
    void ClearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void RegisterUserReturnsViewer() {
        UserRegisterRequestDto request = new UserRegisterRequestDto(
            "  Alice  ",
            "  Alice@Example.com  ",
            "secret123"
        );
        when(userRepository.existsByEmailIgnoreCase("  Alice@Example.com  ")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", 1L);
            ReflectionTestUtils.setField(saved, "createdAt", LocalDateTime.of(2026, 4, 5, 8, 0));
            ReflectionTestUtils.setField(saved, "updatedAt", LocalDateTime.of(2026, 4, 5, 8, 15));
            return saved;
        });

        UserResponseDto result = userService.registerUser(request);

        assertEquals("ALICE", result.name());
        assertEquals("alice@example.com", result.email());
        assertEquals(Role.VIEWER, result.role());
        assertEquals(UserStatus.ACTIVE, result.status());
    }

    @Test
    void RegisterUserThrowsWhenEmailExists() {
        UserRegisterRequestDto request = new UserRegisterRequestDto(
            "Alice",
            "alice@example.com",
            "secret123"
        );
        when(userRepository.existsByEmailIgnoreCase("alice@example.com")).thenReturn(true);

        assertThrows(CustomDuplicateEntityException.class, () -> userService.registerUser(request));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void AuthenticateUserReturnsBearerToken() {
        User user = createUser(3L, "MIA", "mia@example.com", Role.ADMIN, UserStatus.ACTIVE);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("jwt-token");

        JwtResponseDto result = userService.authenticateUser(
            new UserLoginRequestDto("mia@example.com", "secret123")
        );

        assertEquals("jwt-token", result.token());
        assertEquals("Bearer", result.tokenType());
        assertEquals("mia@example.com", result.email());
        assertEquals(Role.ADMIN, result.role());
    }

    @Test
    void AuthenticateUserThrowsForInactiveUser() {
        User user = createUser(4L, "SAM", "sam@example.com", Role.VIEWER, UserStatus.INACTIVE);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        UserLoginRequestDto loginRequest = new UserLoginRequestDto("sam@example.com", "secret123");

        assertThrows(
            AuthorizationDeniedException.class,
            () -> userService.authenticateUser(loginRequest)
        );

        verify(jwtService, never()).generateToken(any(CustomUserDetails.class));
    }

    @Test
    void GetPaginatedUsersReturnsPage() {
        User user = createUser(5L, "ALEX", "alex@example.com", Role.ANALYST, UserStatus.ACTIVE);
        when(userRepository.findAll(any(org.springframework.data.domain.Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(user)));

        Page<UserResponseDto> result = userService.getPaginatedUsers(0, 20, "desc");

        assertEquals(1, result.getTotalElements());
        assertEquals("ALEX", result.getContent().get(0).name());
    }

    @Test
    void UpdateUserReturnsUpdatedDetails() {
        User user = createUser(6L, "ALEX", "alex@example.com", Role.VIEWER, UserStatus.ACTIVE);
        when(userRepository.findById(6L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserResponseDto result = userService.updateUser(
            6L,
            new UpdateUserDto(" Riley ", Role.ADMIN, UserStatus.INACTIVE)
        );

        assertEquals("Riley", result.name());
        assertEquals(Role.ADMIN, result.role());
        assertEquals(UserStatus.INACTIVE, result.status());
    }

    @Test
    void UpdateUserThrowsWhenMissing() {
        when(userRepository.findById(7L)).thenReturn(Optional.empty());
        UpdateUserDto updateUserDto = new UpdateUserDto("Riley", Role.ADMIN, UserStatus.ACTIVE);

        assertThrows(
            EntityNotFoundException.class,
            () -> userService.updateUser(7L, updateUserDto)
        );
    }

    @Test
    void ToggleUserStatusFlipsValue() {
        User user = createUser(8L, "KAI", "kai@example.com", Role.VIEWER, UserStatus.ACTIVE);
        when(userRepository.findById(8L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserResponseDto result = userService.toggleUserStatus(8L);

        assertEquals(UserStatus.INACTIVE, result.status());
    }

    @Test
    void GetCurrentUserIdReturnsAuthenticatedId() {
        User user = createUser(9L, "LIA", "lia@example.com", Role.ADMIN, UserStatus.ACTIVE);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getPrincipal()).thenReturn(new CustomUserDetails(user));

        Long result = userService.getCurrentUserId();

        assertEquals(9L, result);
    }

    @Test
    void GetCurrentUserIdThrowsWithoutPrincipal() {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getPrincipal()).thenReturn("anonymousUser");

        assertThrows(IllegalArgumentException.class, () -> userService.getCurrentUserId());
    }

    private User createUser(
        Long id,
        String name,
        String email,
        Role role,
        UserStatus status
    ) {
        User user = new User(name, email, "encoded-password");
        user.setRole(role);
        user.setStatus(status);
        ReflectionTestUtils.setField(user, "id", id);
        ReflectionTestUtils.setField(user, "createdAt", LocalDateTime.of(2026, 4, 5, 7, 0));
        ReflectionTestUtils.setField(user, "updatedAt", LocalDateTime.of(2026, 4, 5, 7, 30));
        return user;
    }
}
