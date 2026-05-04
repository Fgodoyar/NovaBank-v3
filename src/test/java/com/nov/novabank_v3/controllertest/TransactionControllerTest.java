package com.nov.novabank_v3.controllertest;

import com.nov.novabank_v3.config.SecurityConfig;
import com.nov.novabank_v3.controller.TransactionController;
import com.nov.novabank_v3.dto.TransactionDTO;
import com.nov.novabank_v3.exception.AccountNotFoundException;
import com.nov.novabank_v3.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.nov.novabank_v3.service.TransactionService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
@Import(SecurityConfig.class)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private TransactionDTO transactionDTO;

    @BeforeEach
    void setUp() {
        transactionDTO = TransactionDTO.builder()
                .transactionId(1L)
                .transactionType("transaccion_entrante")
                .amount(new BigDecimal("500.00"))
                .description("Transferencia recibida")
                .creationDate(LocalDateTime.of(2026, 1, 1, 0, 0))
                .accountId(1L)
                .build();
    }

    @Nested
    class findByAccountIdTest {

        @Test
        @WithMockUser
        @DisplayName("GET /api/transactions/transaction/{accountId} → 200 con lista de movimientos")
        void findByAccountId_shouldReturn200WithList() throws Exception {
            when(transactionService.findByAccountId(1L)).thenReturn(List.of(transactionDTO));

            mockMvc.perform(get("/api/transactions/transaction/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].transactionId").value(1L))
                    .andExpect(jsonPath("$[0].transactionType").value("transaccion_entrante"));

            verify(transactionService).findByAccountId(1L);
        }

        @Test
        @WithMockUser
        @DisplayName("GET /api/transactions/transaction/{accountId} → 200 con lista vacía")
        void findByAccountId_shouldReturn200WithEmptyList() throws Exception {
            when(transactionService.findByAccountId(1L)).thenReturn(List.of());

            mockMvc.perform(get("/api/transactions/transaction/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @WithMockUser
        @DisplayName("GET /api/transactions/transaction/{accountId} → 404 cuando la cuenta no existe")
        void findByAccountId_shouldReturn404WhenNotFound() throws Exception {
            when(transactionService.findByAccountId(99L))
                    .thenThrow(new AccountNotFoundException(99));

            mockMvc.perform(get("/api/transactions/transaction/99"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class findByAccountIdAndDateRangeTest {

        @Test
        @WithMockUser
        @DisplayName("GET /api/transactions/account/{accountId}/dates/{startDate}/{endDate} → 200 con lista de movimientos")
        void findByAccountIdAndDateRange_shouldReturn200WithList() throws Exception {

            when(transactionService.findByAccountIdAndCreationDateBetweenOrderByCreationDateDesc(
                    1L,
                    LocalDateTime.of(2026, 1, 1, 0, 0),
                    LocalDateTime.of(2026, 12, 31, 23, 59, 59)
            )).thenReturn(List.of(transactionDTO));

            mockMvc.perform(get("/api/transactions/account/1/dates/2026-01-01T00:00:00/2026-12-31T23:59:59"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].transactionId").value(1L));

            verify(transactionService).findByAccountIdAndCreationDateBetweenOrderByCreationDateDesc(
                    1L,
                    LocalDateTime.of(2026, 1, 1, 0, 0),
                    LocalDateTime.of(2026, 12, 31, 23, 59, 59)
            );
        }

        @Test
        @WithMockUser
        @DisplayName("GET /api/transactions/account/{accountId}/dates/{startDate}/{endDate} → 200 con lista vacía")
        void findByAccountIdAndDateRange_shouldReturn200WithEmptyList() throws Exception {

            LocalDateTime start = LocalDateTime.of(2026, 1, 1, 0, 0);
            LocalDateTime end = LocalDateTime.of(2026, 12, 31, 0, 0);

            when(transactionService.findByAccountIdAndCreationDateBetweenOrderByCreationDateDesc(
                    1L, start, end))
                    .thenReturn(List.of());

            mockMvc.perform(get("/api/transactions/account/1/dates/2026-01-01T00:00:00/2026-12-31T00:00:00"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @WithMockUser
        @DisplayName("GET /api/transactions/account/{accountId}/dates/{startDate}/{endDate} → 404 cuando la cuenta no existe")
        void findByAccountIdAndDateRange_shouldReturn404WhenNotFound() throws Exception {

            LocalDateTime start = LocalDate.of(2026, 1, 1).atStartOfDay();
            LocalDateTime end = LocalDate.of(2026, 12, 31).atStartOfDay();

            when(transactionService.findByAccountIdAndCreationDateBetweenOrderByCreationDateDesc(
                    99L, start, end))
                    .thenThrow(new AccountNotFoundException(99));

            mockMvc.perform(get("/api/transactions/account/99/dates/2026-01-01T00:00:00/2026-12-31T00:00:00"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser
        @DisplayName("GET /api/transactions/account/{accountId}/dates/{startDate}/{endDate} → 400 fecha inválida")
        void findByAccountIdAndDateRange_shouldReturn400WhenInvalidDate() throws Exception {
            mockMvc.perform(get("/api/transactions/account/1/dates/fecha-invalida/2026-12-31"))
                    .andExpect(status().isBadRequest());
        }
    }
}
