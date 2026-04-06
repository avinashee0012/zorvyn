package com.avinashee0012.zorvyn.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.avinashee0012.zorvyn.domain.entity.FinancialRecord;
import com.avinashee0012.zorvyn.domain.entity.User;
import com.avinashee0012.zorvyn.domain.enums.RecordType;
import com.avinashee0012.zorvyn.domain.enums.Role;
import com.avinashee0012.zorvyn.dto.response.CategorySummaryDto;
import com.avinashee0012.zorvyn.dto.response.DashboardSummaryDto;
import com.avinashee0012.zorvyn.dto.response.RecordResponseDto;
import com.avinashee0012.zorvyn.repository.FinancialRecordRepository;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @Mock
    private FinancialRecordRepository financialRecordRepository;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    @Test
    void GetSummaryReturnsTotals() {
        when(financialRecordRepository.sumAmountByType(RecordType.INCOME))
            .thenReturn(new BigDecimal("1200.00"));
        when(financialRecordRepository.sumAmountByType(RecordType.EXPENSE))
            .thenReturn(new BigDecimal("450.00"));
        when(financialRecordRepository.countByDeletedFalse()).thenReturn(7L);

        DashboardSummaryDto result = dashboardService.getSummary();

        assertEquals(new BigDecimal("1200.00"), result.totalIncome());
        assertEquals(new BigDecimal("450.00"), result.totalExpense());
        assertEquals(new BigDecimal("750.00"), result.balance());
        assertEquals(7L, result.totalRecords());
    }

    @Test
    void GetCategoryWiseSummaryThrowsForInvalidType() {
        assertThrows(
            IllegalArgumentException.class,
            () -> dashboardService.getCategoryWiseSummary("bonus")
        );

        verifyNoInteractions(financialRecordRepository);
    }

    @Test
    void GetCategoryWiseSummaryReturnsMappedValues() {
        when(financialRecordRepository.summarizeByCategory(RecordType.EXPENSE))
            .thenReturn(List.<Object[]>of(
                new Object[] {"Food", RecordType.EXPENSE, new BigDecimal("320.00")}
            ));

        List<CategorySummaryDto> result = dashboardService.getCategoryWiseSummary("expense");

        assertEquals(1, result.size());
        assertEquals("Food", result.get(0).category());
        assertEquals(RecordType.EXPENSE, result.get(0).type());
        assertEquals(new BigDecimal("320.00"), result.get(0).totalAmount());
    }

    @Test
    void GetRecentTransactionsReturnsLimitedItems() {
        FinancialRecord first = createRecord(1L, "Salary", RecordType.INCOME, new BigDecimal("900.00"));
        FinancialRecord second = createRecord(2L, "Groceries", RecordType.EXPENSE, new BigDecimal("120.00"));
        when(financialRecordRepository.findTop10ByDeletedFalseOrderByDateDescCreatedAtDesc())
            .thenReturn(List.of(first, second));

        List<RecordResponseDto> result = dashboardService.getRecentTransactions(1);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals("Salary", result.get(0).category());
    }

    @Test
    void GetRecentTransactionsUsesMinimumLimit() {
        FinancialRecord first = createRecord(1L, "Salary", RecordType.INCOME, new BigDecimal("900.00"));
        FinancialRecord second = createRecord(2L, "Rent", RecordType.EXPENSE, new BigDecimal("400.00"));
        when(financialRecordRepository.findTop10ByDeletedFalseOrderByDateDescCreatedAtDesc())
            .thenReturn(List.of(first, second));

        List<RecordResponseDto> result = dashboardService.getRecentTransactions(0);

        assertEquals(1, result.size());
        verify(financialRecordRepository).findTop10ByDeletedFalseOrderByDateDescCreatedAtDesc();
    }

    private FinancialRecord createRecord(
        Long id,
        String category,
        RecordType type,
        BigDecimal amount
    ) {
        User user = new User("Alice", "alice@example.com", "encoded-password");
        user.setRole(Role.ADMIN);
        ReflectionTestUtils.setField(user, "id", 10L);
        ReflectionTestUtils.setField(user, "createdAt", LocalDateTime.of(2026, 4, 5, 9, 0));
        ReflectionTestUtils.setField(user, "updatedAt", LocalDateTime.of(2026, 4, 5, 9, 30));

        FinancialRecord record = new FinancialRecord(
            amount,
            type,
            category,
            LocalDate.of(2026, 4, 5),
            "sample",
            user
        );
        ReflectionTestUtils.setField(record, "id", id);
        ReflectionTestUtils.setField(record, "createdAt", LocalDateTime.of(2026, 4, 5, 10, 0));
        ReflectionTestUtils.setField(record, "updatedAt", LocalDateTime.of(2026, 4, 5, 10, 30));
        return record;
    }
}
