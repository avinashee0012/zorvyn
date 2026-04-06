package com.avinashee0012.zorvyn.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.avinashee0012.zorvyn.domain.enums.RecordType;
import com.avinashee0012.zorvyn.dto.response.RecordResponseDto;
import com.avinashee0012.zorvyn.service.RecordService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RecordService recordService;

    @Test
    @WithMockUser(roles = "VIEWER")
    void GetRecordByIdReturnsRecordForAuthorizedUser() throws Exception {
        when(recordService.getRecordById(21L)).thenReturn(new RecordResponseDto(
            21L,
            new BigDecimal("1250.00"),
            RecordType.INCOME,
            "Salary",
            LocalDate.of(2026, 4, 1),
            "Monthly salary",
            7L,
            "Alex",
            LocalDateTime.of(2026, 4, 1, 10, 0),
            LocalDateTime.of(2026, 4, 1, 10, 5)
        ));

        mockMvc.perform(get("/api/records/21"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(21))
            .andExpect(jsonPath("$.type").value("INCOME"))
            .andExpect(jsonPath("$.category").value("Salary"))
            .andExpect(jsonPath("$.createdByName").value("Alex"));

        verify(recordService).getRecordById(21L);
    }

    @Test
    void GetRecordByIdReturnsUnauthorizedWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/records/21"))
            .andExpect(status().isUnauthorized());
    }
}
