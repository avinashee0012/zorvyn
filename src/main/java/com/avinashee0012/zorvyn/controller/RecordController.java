package com.avinashee0012.zorvyn.controller;

import java.time.LocalDate;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.avinashee0012.zorvyn.dto.request.RecordRequestDto;
import com.avinashee0012.zorvyn.dto.request.UpdateRecordDto;
import com.avinashee0012.zorvyn.dto.response.RecordResponseDto;
import com.avinashee0012.zorvyn.service.RecordService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class RecordController {

    private static final Set<String> SORT_DIRECTIONS = Set.of("ASC", "DESC");

    private final RecordService recordService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    public ResponseEntity<RecordResponseDto> createRecord(
            @RequestBody @Valid RecordRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(recordService.createRecord(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    public ResponseEntity<Page<RecordResponseDto>> getRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        if (!SORT_DIRECTIONS.contains(sortDirection.toUpperCase())) {
            throw new IllegalArgumentException("Invalid sort direction: " + sortDirection);
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(recordService.getPaginatedRecords(page, size, sortBy, sortDirection));
    }

    @GetMapping("/{recordId}")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    public ResponseEntity<RecordResponseDto> getRecordById(@PathVariable Long recordId) {
        return ResponseEntity.status(HttpStatus.OK).body(recordService.getRecordById(recordId));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    public ResponseEntity<Page<RecordResponseDto>> searchRecords(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(recordService.searchRecords(keyword, page, size));
    }

    @GetMapping("/filter")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    public ResponseEntity<Page<RecordResponseDto>> filterRecords(
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(recordService.filterRecords(date, category, type, page, size));
    }

    @PutMapping("/{recordId}")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    public ResponseEntity<RecordResponseDto> updateRecord(
            @PathVariable Long recordId,
            @RequestBody @Valid UpdateRecordDto request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(recordService.updateRecord(recordId, request));
    }

    @DeleteMapping("/{recordId}")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    public ResponseEntity<Void> deleteRecord(@PathVariable Long recordId) {
        recordService.softDeleteRecord(recordId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
