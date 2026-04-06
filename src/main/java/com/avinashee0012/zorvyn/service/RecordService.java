package com.avinashee0012.zorvyn.service;

import java.time.LocalDate;

import org.springframework.data.domain.Page;

import com.avinashee0012.zorvyn.dto.request.RecordRequestDto;
import com.avinashee0012.zorvyn.dto.request.UpdateRecordDto;
import com.avinashee0012.zorvyn.dto.response.RecordResponseDto;

public interface RecordService {

    RecordResponseDto createRecord(RecordRequestDto request);

    RecordResponseDto getRecordById(Long recordId);

    Page<RecordResponseDto> getPaginatedRecords(
        int page,
        int size,
        String sortBy,
        String sortDirection
    );

    Page<RecordResponseDto> searchRecords(String keyword, int page, int size);

    Page<RecordResponseDto> filterRecords(
        LocalDate date,
        String category,
        String type,
        int page,
        int size
    );

    RecordResponseDto updateRecord(Long recordId, UpdateRecordDto request);

    void softDeleteRecord(Long recordId);
}
