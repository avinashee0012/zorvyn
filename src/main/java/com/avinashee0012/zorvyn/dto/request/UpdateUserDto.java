package com.avinashee0012.zorvyn.dto.request;

import com.avinashee0012.zorvyn.domain.enums.Role;
import com.avinashee0012.zorvyn.domain.enums.UserStatus;

import jakarta.validation.constraints.Size;

public record UpdateUserDto(
    @Size(min = 2, message = "Name must be at least 2 characters")
    String name,
    Role role,
    UserStatus status
) {
}
