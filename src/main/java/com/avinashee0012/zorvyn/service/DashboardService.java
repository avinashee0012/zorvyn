package com.avinashee0012.zorvyn.service;

import java.util.List;

import com.avinashee0012.zorvyn.dto.response.CategorySummaryDto;
import com.avinashee0012.zorvyn.dto.response.DashboardSummaryDto;
import com.avinashee0012.zorvyn.dto.response.RecordResponseDto;

public interface DashboardService {

    DashboardSummaryDto getSummary();

    List<CategorySummaryDto> getCategoryWiseSummary(String type);

    List<RecordResponseDto> getRecentTransactions(int limit);
}
