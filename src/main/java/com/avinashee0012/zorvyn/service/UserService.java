package com.avinashee0012.zorvyn.service;

import org.springframework.data.domain.Page;

import com.avinashee0012.zorvyn.dto.request.UserLoginRequestDto;
import com.avinashee0012.zorvyn.dto.request.UpdateUserDto;
import com.avinashee0012.zorvyn.dto.request.UserRegisterRequestDto;
import com.avinashee0012.zorvyn.dto.response.JwtResponseDto;
import com.avinashee0012.zorvyn.dto.response.UserResponseDto;

public interface UserService {

    UserResponseDto registerUser(UserRegisterRequestDto request);

    JwtResponseDto authenticateUser(UserLoginRequestDto request);

    Page<UserResponseDto> getPaginatedUsers(int page, int size, String sort);

    UserResponseDto updateUser(Long userId, UpdateUserDto request);

    UserResponseDto toggleUserStatus(Long userId);

    Long getCurrentUserId();
}
