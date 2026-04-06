package com.avinashee0012.zorvyn.dto.response;

import com.avinashee0012.zorvyn.domain.enums.Role;

public record JwtResponseDto(
    String token,
    String tokenType,
    String email,
    Role role
) {
}
