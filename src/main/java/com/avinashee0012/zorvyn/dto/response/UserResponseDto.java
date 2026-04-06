package com.avinashee0012.zorvyn.dto.response;

import java.time.LocalDateTime;

import com.avinashee0012.zorvyn.domain.enums.Role;
import com.avinashee0012.zorvyn.domain.enums.UserStatus;

public record UserResponseDto(
    Long id,
    String name,
    String email,
    Role role,
    UserStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
