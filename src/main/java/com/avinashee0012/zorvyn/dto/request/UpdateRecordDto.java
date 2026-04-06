package com.avinashee0012.zorvyn.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.avinashee0012.zorvyn.domain.enums.RecordType;

import jakarta.validation.constraints.DecimalMin;

public record UpdateRecordDto(
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    BigDecimal amount,
    RecordType type,
    String category,
    LocalDate date,
    String description
) {
}
