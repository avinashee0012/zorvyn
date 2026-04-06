package com.avinashee0012.zorvyn.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.avinashee0012.zorvyn.domain.enums.RecordType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RecordRequestDto(
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    BigDecimal amount,
    @NotNull(message = "Type is required")
    RecordType type,
    @NotBlank(message = "Category is required")
    @Size(max = 100, message = "Category must not exceed 100 characters")
    String category,
    @NotNull(message = "Date is required")
    LocalDate date,
    @Size(max = 500, message = "Description must not exceed 500 characters")
    String description
) {
}
