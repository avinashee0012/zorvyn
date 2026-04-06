package com.avinashee0012.zorvyn.service.impl;

import java.time.LocalDate;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.avinashee0012.zorvyn.domain.entity.FinancialRecord;
import com.avinashee0012.zorvyn.domain.entity.User;
import com.avinashee0012.zorvyn.domain.enums.RecordType;
import com.avinashee0012.zorvyn.dto.request.RecordRequestDto;
import com.avinashee0012.zorvyn.dto.request.UpdateRecordDto;
import com.avinashee0012.zorvyn.dto.response.RecordResponseDto;
import com.avinashee0012.zorvyn.repository.FinancialRecordRepository;
import com.avinashee0012.zorvyn.repository.UserRepository;
import com.avinashee0012.zorvyn.service.RecordService;
import com.avinashee0012.zorvyn.service.UserService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class RecordServiceImpl implements RecordService {

    private static final Set<String> ALLOWED_SORT_FIELDS =
        Set.of("date", "amount", "category", "createdAt");

    private final FinancialRecordRepository financialRecordRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    public RecordResponseDto createRecord(RecordRequestDto request) {
        User currentUser = userRepository.findById(userService.getCurrentUserId())
            .orElseThrow(() -> new EntityNotFoundException("Current user not found"));

        FinancialRecord financialRecord = new FinancialRecord(
            request.amount(),
            request.type(),
            request.category().trim(),
            request.date(),
            request.description(),
            currentUser
        );

        return toResponse(financialRecordRepository.save(financialRecord));
    }

    @Override
    @Transactional(readOnly = true)
    public RecordResponseDto getRecordById(Long recordId) {
        return toResponse(getActiveRecord(recordId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RecordResponseDto> getPaginatedRecords(
        int page,
        int size,
        String sortBy,
        String sortDirection
    ) {
        Pageable pageable = buildPageable(page, size, sortBy, sortDirection);
        return financialRecordRepository.findByDeletedFalse(pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RecordResponseDto> searchRecords(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100));
        return financialRecordRepository.searchActiveRecords(keyword, pageable)
            .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RecordResponseDto> filterRecords(
        LocalDate date,
        String category,
        String type,
        int page,
        int size
    ) {
        Pageable pageable = PageRequest.of(
            Math.max(page, 0),
            Math.min(Math.max(size, 1), 100),
            Sort.by("date").descending()
        );
        RecordType recordType = type == null || type.isBlank()
            ? null
            : RecordType.valueOf(type.toUpperCase());
        return financialRecordRepository
            .filterRecords(date, normalize(category), recordType, pageable)
            .map(this::toResponse);
    }

    @Override
    public RecordResponseDto updateRecord(Long recordId, UpdateRecordDto request) {
        FinancialRecord financialRecord = getActiveRecord(recordId);
        financialRecord.update(
            request.amount(),
            request.type(),
            request.category(),
            request.date(),
            request.description()
        );
        return toResponse(financialRecordRepository.save(financialRecord));
    }

    @Override
    public void softDeleteRecord(Long recordId) {
        FinancialRecord financialRecord = getActiveRecord(recordId);
        financialRecord.softDelete();
        financialRecordRepository.save(financialRecord);
    }

    private FinancialRecord getActiveRecord(Long recordId) {
        FinancialRecord financialRecord = financialRecordRepository.findById(recordId)
            .orElseThrow(
                () -> new EntityNotFoundException("Record not found with id: " + recordId)
            );
        if (financialRecord.isDeleted()) {
            throw new EntityNotFoundException("Record not found with id: " + recordId);
        }
        return financialRecord;
    }

    private Pageable buildPageable(int page, int size, String sortBy, String sortDirection) {
        String normalizedSortBy = normalize(sortBy);
        if (normalizedSortBy == null || !ALLOWED_SORT_FIELDS.contains(normalizedSortBy)) {
            throw new IllegalArgumentException("Invalid sort field: " + sortBy);
        }

        Sort.Direction direction = "asc".equalsIgnoreCase(sortDirection)
            ? Sort.Direction.ASC
            : Sort.Direction.DESC;
        return PageRequest.of(
            Math.max(page, 0),
            Math.min(Math.max(size, 1), 100),
            Sort.by(direction, normalizedSortBy)
        );
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
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
