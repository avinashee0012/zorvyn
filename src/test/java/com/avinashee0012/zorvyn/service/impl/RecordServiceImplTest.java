package com.avinashee0012.zorvyn.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import com.avinashee0012.zorvyn.domain.entity.FinancialRecord;
import com.avinashee0012.zorvyn.domain.entity.User;
import com.avinashee0012.zorvyn.domain.enums.RecordType;
import com.avinashee0012.zorvyn.domain.enums.Role;
import com.avinashee0012.zorvyn.dto.request.RecordRequestDto;
import com.avinashee0012.zorvyn.dto.request.UpdateRecordDto;
import com.avinashee0012.zorvyn.dto.response.RecordResponseDto;
import com.avinashee0012.zorvyn.repository.FinancialRecordRepository;
import com.avinashee0012.zorvyn.repository.UserRepository;
import com.avinashee0012.zorvyn.service.UserService;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class RecordServiceImplTest {

    @Mock
    private FinancialRecordRepository financialRecordRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private RecordServiceImpl recordService;

    @Test
    void CreateRecordReturnsSavedRecord() {
        User user = createUser(7L, "Alex");
        RecordRequestDto request = new RecordRequestDto(
            new BigDecimal("150.00"),
            RecordType.EXPENSE,
            "  Food  ",
            LocalDate.of(2026, 4, 5),
            "Dinner"
        );
        when(userService.getCurrentUserId()).thenReturn(7L);
        when(userRepository.findById(7L)).thenReturn(Optional.of(user));
        when(financialRecordRepository.save(any(FinancialRecord.class)))
            .thenAnswer(invocation -> {
                FinancialRecord saved = invocation.getArgument(0);
                ReflectionTestUtils.setField(saved, "id", 11L);
                ReflectionTestUtils.setField(
                    saved,
                    "createdAt",
                    LocalDateTime.of(2026, 4, 5, 12, 0)
                );
                ReflectionTestUtils.setField(
                    saved,
                    "updatedAt",
                    LocalDateTime.of(2026, 4, 5, 12, 5)
                );
                return saved;
            });

        RecordResponseDto result = recordService.createRecord(request);

        assertEquals(11L, result.id());
        assertEquals("Food", result.category());
        assertEquals(7L, result.createdById());
        assertEquals("Alex", result.createdByName());
    }

    @Test
    void CreateRecordThrowsWhenCurrentUserMissing() {
        RecordRequestDto request = new RecordRequestDto(
            new BigDecimal("150.00"),
            RecordType.EXPENSE,
            "Food",
            LocalDate.of(2026, 4, 5),
            "Dinner"
        );
        when(userService.getCurrentUserId()).thenReturn(99L);
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> recordService.createRecord(request));

        verify(financialRecordRepository, never()).save(any(FinancialRecord.class));
    }

    @Test
    void GetPaginatedRecordsReturnsMappedPage() {
        FinancialRecord financialRecord = createRecord(12L, createUser(7L, "Alex"), false);
        when(financialRecordRepository.findByDeletedFalse(any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(financialRecord)));

        Page<RecordResponseDto> result = recordService.getPaginatedRecords(1, 5, "date", "asc");

        assertEquals(1, result.getTotalElements());
        assertEquals(12L, result.getContent().get(0).id());
        assertEquals("Travel", result.getContent().get(0).category());
    }

    @Test
    void GetPaginatedRecordsThrowsForInvalidSort() {
        assertThrows(
            IllegalArgumentException.class,
            () -> recordService.getPaginatedRecords(0, 10, "status", "asc")
        );

        verify(financialRecordRepository, never()).findByDeletedFalse(any(Pageable.class));
    }

    @Test
    void GetRecordByIdReturnsMappedRecord() {
        FinancialRecord financialRecord = createRecord(13L, createUser(7L, "Alex"), false);
        when(financialRecordRepository.findById(13L)).thenReturn(Optional.of(financialRecord));

        RecordResponseDto result = recordService.getRecordById(13L);

        assertEquals(13L, result.id());
        assertEquals("Travel", result.category());
        assertEquals("Alex", result.createdByName());
    }

    @Test
    void GetRecordByIdThrowsForDeletedRecord() {
        FinancialRecord financialRecord = createRecord(15L, createUser(7L, "Alex"), true);
        when(financialRecordRepository.findById(15L)).thenReturn(Optional.of(financialRecord));

        assertThrows(EntityNotFoundException.class, () -> recordService.getRecordById(15L));
    }

    @Test
    void SearchRecordsReturnsMappedPage() {
        FinancialRecord financialRecord = createRecord(14L, createUser(3L, "Mia"), false);
        when(financialRecordRepository.searchActiveRecords(eq("food"), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(financialRecord)));

        Page<RecordResponseDto> result = recordService.searchRecords("food", 0, 20);

        assertEquals(1, result.getContent().size());
        assertEquals(14L, result.getContent().get(0).id());
    }

    @Test
    void FilterRecordsReturnsFilteredPage() {
        FinancialRecord financialRecord = createRecord(20L, createUser(8L, "Riya"), false);
        when(financialRecordRepository.filterRecords(
            eq(LocalDate.of(2026, 4, 5)),
            eq("Travel"),
            eq(RecordType.EXPENSE),
            any(Pageable.class)
        )).thenReturn(new PageImpl<>(List.of(financialRecord)));

        Page<RecordResponseDto> result = recordService.filterRecords(
            LocalDate.of(2026, 4, 5),
            "  Travel ",
            "expense",
            0,
            10
        );

        assertEquals(1, result.getContent().size());
        assertEquals(RecordType.EXPENSE, result.getContent().get(0).type());
    }

    @Test
    void FilterRecordsThrowsForInvalidType() {
        assertThrows(
            IllegalArgumentException.class,
            () -> recordService.filterRecords(null, "Food", "bonus", 0, 10)
        );

        verify(financialRecordRepository, never()).filterRecords(any(), any(), any(), any());
    }

    @Test
    void UpdateRecordReturnsUpdatedRecord() {
        User user = createUser(5L, "Noah");
        FinancialRecord financialRecord = createRecord(30L, user, false);
        UpdateRecordDto request = new UpdateRecordDto(
            new BigDecimal("210.00"),
            RecordType.INCOME,
            " Salary ",
            LocalDate.of(2026, 4, 6),
            "Updated"
        );
        when(financialRecordRepository.findById(30L)).thenReturn(Optional.of(financialRecord));
        when(financialRecordRepository.save(financialRecord)).thenReturn(financialRecord);

        RecordResponseDto result = recordService.updateRecord(30L, request);

        assertEquals(new BigDecimal("210.00"), result.amount());
        assertEquals(RecordType.INCOME, result.type());
        assertEquals("Salary", result.category());
        assertEquals(LocalDate.of(2026, 4, 6), result.date());
    }

    @Test
    void UpdateRecordThrowsForDeletedRecord() {
        FinancialRecord financialRecord = createRecord(31L, createUser(5L, "Noah"), true);
        UpdateRecordDto request = new UpdateRecordDto(
            new BigDecimal("210.00"),
            RecordType.INCOME,
            "Salary",
            LocalDate.of(2026, 4, 6),
            "Updated"
        );
        when(financialRecordRepository.findById(31L)).thenReturn(Optional.of(financialRecord));

        assertThrows(EntityNotFoundException.class, () -> recordService.updateRecord(31L, request));

        verify(financialRecordRepository, never()).save(any(FinancialRecord.class));
    }

    @Test
    void SoftDeleteRecordMarksRecordDeleted() {
        FinancialRecord financialRecord = createRecord(41L, createUser(4L, "Sara"), false);
        when(financialRecordRepository.findById(41L)).thenReturn(Optional.of(financialRecord));
        when(financialRecordRepository.save(financialRecord)).thenReturn(financialRecord);

        recordService.softDeleteRecord(41L);

        assertTrueDeleted(financialRecord);
        verify(financialRecordRepository).save(financialRecord);
    }

    @Test
    void SoftDeleteRecordThrowsWhenMissing() {
        when(financialRecordRepository.findById(42L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> recordService.softDeleteRecord(42L));

        verify(financialRecordRepository, never()).save(any(FinancialRecord.class));
    }

    private void assertTrueDeleted(FinancialRecord financialRecord) {
        assertEquals(true, financialRecord.isDeleted());
        assertFalse(!financialRecord.isDeleted());
    }

    private User createUser(Long id, String name) {
        User user = new User(name, name.toLowerCase() + "@example.com", "encoded-password");
        user.setRole(Role.ADMIN);
        ReflectionTestUtils.setField(user, "id", id);
        ReflectionTestUtils.setField(user, "createdAt", LocalDateTime.of(2026, 4, 5, 9, 0));
        ReflectionTestUtils.setField(user, "updatedAt", LocalDateTime.of(2026, 4, 5, 9, 30));
        return user;
    }

    private FinancialRecord createRecord(Long id, User user, boolean deleted) {
        FinancialRecord financialRecord = new FinancialRecord(
            new BigDecimal("120.00"),
            RecordType.EXPENSE,
            "Travel",
            LocalDate.of(2026, 4, 5),
            "Trip",
            user
        );
        ReflectionTestUtils.setField(financialRecord, "id", id);
        ReflectionTestUtils.setField(financialRecord, "deleted", deleted);
        ReflectionTestUtils.setField(financialRecord, "createdAt", LocalDateTime.of(2026, 4, 5, 11, 0));
        ReflectionTestUtils.setField(financialRecord, "updatedAt", LocalDateTime.of(2026, 4, 5, 11, 30));
        return financialRecord;
    }
}
