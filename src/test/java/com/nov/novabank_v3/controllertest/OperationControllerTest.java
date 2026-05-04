package com.nov.novabank_v3.controllertest;

import com.nov.novabank_v3.config.SecurityConfig;
import com.nov.novabank_v3.controller.OperationController;
import com.nov.novabank_v3.dto.TransactionDTO;
import com.nov.novabank_v3.exception.AccountNotFoundException;
import com.nov.novabank_v3.exception.InsufficientBalanceException;
import com.nov.novabank_v3.service.JwtService;
import com.nov.novabank_v3.service.OperationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(OperationController.class)
@Import(SecurityConfig.class)
public class OperationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OperationService operationService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private TransactionDTO transactionDTO;

    private static final String VALID_OPERATION_JSON = """
            {
              "accountNumber": "ES9121000000000000000002",
              "amount": 500.00
            }
            """;

    private static final String VALID_TRANSFER_JSON = """
            {
              "sourceAccount": "ES9121000000000000000002",
              "destinationAccount": "ES9121000000000000000003",
              "amount": 500.00
            }
            """;

    @BeforeEach
    void setUp() {
        transactionDTO = TransactionDTO.builder()
                .transactionId(1L)
                .transactionType("transaccion_entrante")
                .amount(new BigDecimal("500.00"))
                .description("Operación realizada")
                .creationDate(LocalDateTime.of(2026, 1, 1, 0, 0))
                .accountId(1L)
                .build();
    }

    @Nested
    class depositTest {

        @Test
        @WithMockUser
        @DisplayName("POST /api/operations/deposit → 200 con transacción creada")
        void deposit_shouldReturn200WhenValid() throws Exception {
            when(operationService.deposit(any(), any())).thenReturn(transactionDTO);

            mockMvc.perform(post("/api/operations/deposit")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_OPERATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.transactionId").value(1L))
                    .andExpect(jsonPath("$.amount").value(500.00));

            verify(operationService).deposit(any(), any());
        }

        @Test
        @WithMockUser
        @DisplayName("POST /api/operations/deposit → 400 si el body no supera la validación @Valid")
        void deposit_shouldReturn400WhenInvalid() throws Exception {
            mockMvc.perform(post("/api/operations/deposit")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(operationService);
        }

        @Test
        @WithMockUser
        @DisplayName("POST /api/operations/deposit → 404 si la cuenta no existe")
        void deposit_shouldReturn404WhenAccountNotFound() throws Exception {
            when(operationService.deposit(any(), any()))
                    .thenThrow(new AccountNotFoundException("ES9121000000000000000002"));

            mockMvc.perform(post("/api/operations/deposit")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_OPERATION_JSON))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("POST /api/operations/deposit → 403 sin autenticación")
        void deposit_shouldReturn403WhenUnauthenticated() throws Exception {
            mockMvc.perform(post("/api/operations/deposit")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_OPERATION_JSON))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(operationService);
        }
    }

    @Nested
    class withdrawalTest {

        @Test
        @WithMockUser
        @DisplayName("POST /api/operations/withdrawal → 200 con transacción creada")
        void withdrawal_shouldReturn200WhenValid() throws Exception {
            when(operationService.withdraw(any(), any())).thenReturn(transactionDTO);

            mockMvc.perform(post("/api/operations/withdrawal")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_OPERATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.transactionId").value(1L))
                    .andExpect(jsonPath("$.amount").value(500.00));

            verify(operationService).withdraw(any(), any());
        }

        @Test
        @WithMockUser
        @DisplayName("POST /api/operations/withdrawal → 400 si el body no supera la validación @Valid")
        void withdrawal_shouldReturn400WhenInvalid() throws Exception {
            mockMvc.perform(post("/api/operations/withdrawal")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(operationService);
        }

        @Test
        @WithMockUser
        @DisplayName("POST /api/operations/withdrawal → 404 si la cuenta no existe")
        void withdrawal_shouldReturn404WhenAccountNotFound() throws Exception {
            when(operationService.withdraw(any(), any()))
                    .thenThrow(new AccountNotFoundException("ES9121000000000000000002"));

            mockMvc.perform(post("/api/operations/withdrawal")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_OPERATION_JSON))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser
        @DisplayName("POST /api/operations/withdrawal → 422 si saldo insuficiente")
        void withdrawal_shouldReturn422WhenInsufficientBalance() throws Exception {
            when(operationService.withdraw(any(), any()))
                    .thenThrow(new InsufficientBalanceException("ES9121000000000000000002",
                            new BigDecimal("100.00"), new BigDecimal("500.00")));

            mockMvc.perform(post("/api/operations/withdrawal")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_OPERATION_JSON))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    class transferTest {

        @Test
        @WithMockUser
        @DisplayName("POST /api/operations/transfer → 200 con transacciones creadas")
        void transfer_shouldReturn200WhenValid() throws Exception {
            when(operationService.transfer(any(), any(), any())).thenReturn(List.of(transactionDTO, transactionDTO));

            mockMvc.perform(post("/api/operations/transfer")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_TRANSFER_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));

            verify(operationService).transfer(any(), any(), any());
        }

        @Test
        @WithMockUser
        @DisplayName("POST /api/operations/transfer → 400 si el body no supera la validación @Valid")
        void transfer_shouldReturn400WhenInvalid() throws Exception {
            mockMvc.perform(post("/api/operations/transfer")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(operationService);
        }

        @Test
        @WithMockUser
        @DisplayName("POST /api/operations/transfer → 404 si alguna cuenta no existe")
        void transfer_shouldReturn404WhenAccountNotFound() throws Exception {
            when(operationService.transfer(any(), any(), any()))
                    .thenThrow(new AccountNotFoundException("ES9121000000000000000002"));

            mockMvc.perform(post("/api/operations/transfer")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_TRANSFER_JSON))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser
        @DisplayName("POST /api/operations/transfer → 422 si saldo insuficiente")
        void transfer_shouldReturn422WhenInsufficientBalance() throws Exception {
            when(operationService.transfer(any(), any(), any()))
                    .thenThrow(new InsufficientBalanceException("ES9121000000000000000002",
                            new BigDecimal("100.00"), new BigDecimal("500.00")));

            mockMvc.perform(post("/api/operations/transfer")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_TRANSFER_JSON))
                    .andExpect(status().isUnprocessableEntity());
        }
    }
}