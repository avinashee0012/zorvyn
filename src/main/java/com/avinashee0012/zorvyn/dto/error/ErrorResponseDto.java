package com.avinashee0012.zorvyn.dto.error;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.http.HttpStatus;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;

@Getter
public class ErrorResponseDto {
    private final LocalDateTime timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final String path;

    public ErrorResponseDto(HttpStatus status, String message, HttpServletRequest request) {
        this.timestamp = LocalDateTime.now(ZoneOffset.UTC);
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.message = message;
        this.path = request.getMethod() + " " + request.getRequestURI();
    }
}
