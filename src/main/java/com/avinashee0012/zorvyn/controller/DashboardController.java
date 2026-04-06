package com.avinashee0012.zorvyn.controller;

import com.avinashee0012.zorvyn.dto.response.CategorySummaryDto;
import com.avinashee0012.zorvyn.dto.response.DashboardSummaryDto;
import com.avinashee0012.zorvyn.dto.response.RecordResponseDto;
import com.avinashee0012.zorvyn.service.DashboardService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    public ResponseEntity<DashboardSummaryDto> getSummary() {
        return ResponseEntity.status(HttpStatus.OK).body(dashboardService.getSummary());
    }

    @GetMapping("/categories")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    public ResponseEntity<List<CategorySummaryDto>> getCategoryWiseSummary(
            @RequestParam(required = false) String type) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(dashboardService.getCategoryWiseSummary(type));
    }

    @GetMapping("/recent-transactions")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    public ResponseEntity<List<RecordResponseDto>> getRecentTransactions(
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(dashboardService.getRecentTransactions(limit));
    }
}
