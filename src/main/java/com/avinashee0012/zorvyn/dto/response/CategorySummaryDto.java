package com.avinashee0012.zorvyn.dto.response;

import java.math.BigDecimal;

import com.avinashee0012.zorvyn.domain.enums.RecordType;

public record CategorySummaryDto(
    String category,
    RecordType type,
    BigDecimal totalAmount
) {
}
