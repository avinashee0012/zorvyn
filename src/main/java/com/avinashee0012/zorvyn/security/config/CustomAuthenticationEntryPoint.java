package com.avinashee0012.zorvyn.security.config;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.avinashee0012.zorvyn.dto.error.ErrorResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final String DEFAULT_MESSAGE =
        "Authentication is required to access this resource";

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException authException
    ) throws IOException {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ErrorResponseDto errorResponse = new ErrorResponseDto(
            status,
            resolveMessage(authException),
            request
        );

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }

    private String resolveMessage(AuthenticationException authException) {
        if (authException == null || authException.getMessage() == null
            || authException.getMessage().isBlank()) {
            return DEFAULT_MESSAGE;
        }
        return authException.getMessage();
    }
}
