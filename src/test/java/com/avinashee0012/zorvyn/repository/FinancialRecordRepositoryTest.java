package com.avinashee0012.zorvyn.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import com.avinashee0012.zorvyn.domain.entity.FinancialRecord;
import com.avinashee0012.zorvyn.domain.entity.User;
import com.avinashee0012.zorvyn.domain.enums.RecordType;

@DataJpaTest
@ActiveProfiles("test")
class FinancialRecordRepositoryTest {

    @Autowired
    private FinancialRecordRepository financialRecordRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void SearchActiveRecordsExcludesSoftDeletedRows() {
        User user = userRepository.save(new User("ALEX", "alex.repo@example.com", "encoded"));

        financialRecordRepository.save(new FinancialRecord(
            new BigDecimal("200.00"),
            RecordType.EXPENSE,
            "Groceries",
            LocalDate.of(2026, 4, 5),
            "Weekly food shopping",
            user
        ));

        FinancialRecord deletedRecord = financialRecordRepository.save(new FinancialRecord(
            new BigDecimal("150.00"),
            RecordType.EXPENSE,
            "Groceries",
            LocalDate.of(2026, 4, 6),
            "Deleted grocery entry",
            user
        ));
        deletedRecord.softDelete();
        financialRecordRepository.save(deletedRecord);

        Page<FinancialRecord> result = financialRecordRepository.searchActiveRecords(
            "grocer",
            PageRequest.of(0, 10)
        );

        assertEquals(1, result.getTotalElements());
        assertFalse(result.getContent().get(0).isDeleted());
        assertEquals("Groceries", result.getContent().get(0).getCategory());
    }

    @Test
    void SummarizeByCategoryAggregatesOnlyActiveRecords() {
        User user = userRepository.save(new User("MIA", "mia.repo@example.com", "encoded"));

        financialRecordRepository.save(new FinancialRecord(
            new BigDecimal("500.00"),
            RecordType.EXPENSE,
            "Travel",
            LocalDate.of(2026, 4, 1),
            "Flight",
            user
        ));
        financialRecordRepository.save(new FinancialRecord(
            new BigDecimal("300.00"),
            RecordType.EXPENSE,
            "Travel",
            LocalDate.of(2026, 4, 2),
            "Hotel",
            user
        ));

        FinancialRecord deletedRecord = financialRecordRepository.save(new FinancialRecord(
            new BigDecimal("900.00"),
            RecordType.EXPENSE,
            "Travel",
            LocalDate.of(2026, 4, 3),
            "Cancelled trip",
            user
        ));
        deletedRecord.softDelete();
        financialRecordRepository.save(deletedRecord);

        List<Object[]> summary = financialRecordRepository.summarizeByCategory(RecordType.EXPENSE);

        assertEquals(1, summary.size());
        assertEquals("Travel", summary.get(0)[0]);
        assertEquals(RecordType.EXPENSE, summary.get(0)[1]);
        assertEquals(new BigDecimal("800.00"), summary.get(0)[2]);
    }
}
