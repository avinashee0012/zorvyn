package com.avinashee0012.zorvyn.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.avinashee0012.zorvyn.dto.error.ErrorResponseDto;

import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ CustomDuplicateEntityException.class, IllegalStateException.class })
    public ResponseEntity<ErrorResponseDto> handleDuplicateException(
            Exception ex,
            HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        ErrorResponseDto response = new ErrorResponseDto(status, ex.getMessage(), request);
        log.warn("CONFLICT: {} [{} {}] - {}", status.value(), request.getMethod(), request.getRequestURI(),
                ex.getMessage());
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler({ ValidationException.class, MethodArgumentNotValidException.class,
            IllegalArgumentException.class })
    public ResponseEntity<ErrorResponseDto> handleValidationException(
            Exception ex,
            HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponseDto response = new ErrorResponseDto(status, ex.getMessage(), request);
        log.warn("BAD_REQUEST: {} [{} {}] - {}", status.value(), request.getMethod(), request.getRequestURI(),
                ex.getMessage());
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler({ EntityNotFoundException.class })
    public ResponseEntity<ErrorResponseDto> handleResourceNotFound(
            Exception ex,
            HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponseDto response = new ErrorResponseDto(status, ex.getMessage(), request);
        log.warn("NOT_FOUND: {} [{} {}] - {}", status.value(), request.getMethod(), request.getRequestURI(),
                ex.getMessage());
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler({ AccessDeniedException.class, AuthorizationDeniedException.class })
    public ResponseEntity<ErrorResponseDto> handleAccessDenied(
            Exception ex,
            HttpServletRequest request) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        ErrorResponseDto response = new ErrorResponseDto(status, ex.getMessage(), request);
        log.warn("FORBIDDEN: {} [{} {}] - {}", status.value(), request.getMethod(),
                request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler({ AuthenticationException.class, JwtException.class })
    public ResponseEntity<ErrorResponseDto> handleAuthenticationException(
            Exception ex,
            HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ErrorResponseDto response = new ErrorResponseDto(status, ex.getMessage(), request);
        log.warn("UNAUTHORIZED: {} [{} {}]", status.value(), request.getMethod(), request.getRequestURI());
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(
            Exception ex,
            HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponseDto response = new ErrorResponseDto(status, ex.getMessage(), request);
        log.error("INTERNAL_SERVER_ERROR: {} [{} {}]", status.value(), request.getMethod(), request.getRequestURI(),
                ex);
        return ResponseEntity.status(status).body(response);
    }

}
