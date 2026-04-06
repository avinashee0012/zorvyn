package com.avinashee0012.zorvyn.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.avinashee0012.zorvyn.domain.enums.RecordType;

public record RecordResponseDto(
    Long id,
    BigDecimal amount,
    RecordType type,
    String category,
    LocalDate date,
    String description,
    Long createdById,
    String createdByName,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
