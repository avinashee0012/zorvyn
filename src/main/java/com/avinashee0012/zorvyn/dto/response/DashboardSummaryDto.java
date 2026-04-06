package com.avinashee0012.zorvyn.dto.response;

import java.math.BigDecimal;

public record DashboardSummaryDto(
    BigDecimal totalIncome,
    BigDecimal totalExpense,
    BigDecimal balance,
    long totalRecords
) {
}
