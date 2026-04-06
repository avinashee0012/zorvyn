package com.avinashee0012.zorvyn.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.avinashee0012.zorvyn.domain.entity.FinancialRecord;
import com.avinashee0012.zorvyn.domain.enums.RecordType;
import com.avinashee0012.zorvyn.dto.response.CategorySummaryDto;
import com.avinashee0012.zorvyn.dto.response.DashboardSummaryDto;
import com.avinashee0012.zorvyn.dto.response.RecordResponseDto;
import com.avinashee0012.zorvyn.repository.FinancialRecordRepository;
import com.avinashee0012.zorvyn.service.DashboardService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final FinancialRecordRepository financialRecordRepository;

    @Override
    public DashboardSummaryDto getSummary() {
        BigDecimal totalIncome = financialRecordRepository.sumAmountByType(RecordType.INCOME);
        BigDecimal totalExpense = financialRecordRepository.sumAmountByType(RecordType.EXPENSE);
        return new DashboardSummaryDto(
            totalIncome,
            totalExpense,
            totalIncome.subtract(totalExpense),
            financialRecordRepository.countByDeletedFalse()
        );
    }

    @Override
    public List<CategorySummaryDto> getCategoryWiseSummary(String type) {
        RecordType recordType = type == null || type.isBlank()
            ? null
            : RecordType.valueOf(type.toUpperCase());
        return financialRecordRepository.summarizeByCategory(recordType).stream()
            .map(result -> new CategorySummaryDto(
                (String) result[0],
                (RecordType) result[1],
                (BigDecimal) result[2]
            ))
            .toList();
    }

    @Override
    public List<RecordResponseDto> getRecentTransactions(int limit) {
        List<FinancialRecord> records =
            financialRecordRepository.findTop10ByDeletedFalseOrderByDateDescCreatedAtDesc();
        return records.stream()
            .limit(Math.max(limit, 1))
            .map(this::toResponse)
            .toList();
    }

    private RecordResponseDto toResponse(FinancialRecord financialRecord) {
        return new RecordResponseDto(
            financialRecord.getId(),
            financialRecord.getAmount(),
            financialRecord.getType(),
            financialRecord.getCategory(),
            financialRecord.getDate(),
            financialRecord.getDescription(),
            financialRecord.getCreatedBy().getId(),
            financialRecord.getCreatedBy().getName(),
            financialRecord.getCreatedAt(),
            financialRecord.getUpdatedAt()
        );
    }
}
