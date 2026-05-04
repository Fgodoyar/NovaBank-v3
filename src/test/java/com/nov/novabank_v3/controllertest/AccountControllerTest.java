package com.nov.novabank_v3.controllertest;

import com.nov.novabank_v3.config.SecurityConfig;
import com.nov.novabank_v3.controller.AccountController;
import com.nov.novabank_v3.dto.AccountDTO;
import com.nov.novabank_v3.exception.AccountNotFoundException;
import com.nov.novabank_v3.service.AccountService;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AccountController.class)
@Import(SecurityConfig.class)
public class AccountControllerTest {

    @MockitoBean
    private AccountService accountService;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private AccountDTO accountDTO;

    @BeforeEach
    void setUp() {
        accountDTO = AccountDTO.builder()
                .account_id(1L)
                .account_number("ES9121000000000000000002")
                .account_holder("Juan Bartolomeo García")
                .balance(new BigDecimal("1500.00"))
                .creation_date(LocalDateTime.of(2026, 1, 1, 0, 0))
                .customer_id(1L)
                .build();
    }

    @Nested
    class createAccount{

        @Test
        @WithMockUser
        @DisplayName("POST /api/accounts → 201 con cuenta creada")
        void createAccount_shouldReturn201WhenValid() throws Exception {
            when(accountService.createAccount(1L)).thenReturn(accountDTO);

            mockMvc.perform(post("/api/accounts")
                            .param("customerId", "1"))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.account_id").value(1L))
                    .andExpect(jsonPath("$.account_number").value("ES9121000000000000000002"));

            verify(accountService).createAccount(1L);
        }

        @Test
        @WithMockUser
        @DisplayName("POST /api/accounts → 400 si el body no supera la validación @Valid")
        void createAccount_shouldReturn400WhenInvalid() throws Exception {
            mockMvc.perform(post("/api/accounts"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(accountService);
        }

        @Test
        @WithMockUser
        @DisplayName("POST /api/accounts → 400 si el cliente ya tiene una cuenta")
        void createAccount_shouldReturn400WhenDuplicate() throws Exception {
            when(accountService.createAccount(1L))
                    .thenThrow(new IllegalArgumentException("Cliente ya registrado"));

            mockMvc.perform(post("/api/accounts")
                            .param("customerId", "1"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("POST /api/accounts → 403 sin autenticación")
        void createAccount_shouldReturn403WhenUnauthenticated() throws Exception {
            mockMvc.perform(post("/api/accounts")
                            .param("customerId", "1"))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(accountService);
        }
    }

    @Nested
    class findByAccountNumberTest{

        @Test
        @WithMockUser
        @DisplayName("GET /api/accounts/number/{accountNumber} → 200 cuando la cuenta se encuentra")
        void findByAccountNumber_shouldReturn200WhenFound() throws Exception {
            when(accountService.findByAccountNumber("ES9121000000000000000002")).thenReturn(accountDTO);

            mockMvc.perform(get("/api/accounts/number/ES9121000000000000000002"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.account_number").value("ES9121000000000000000002"));

            verify(accountService).findByAccountNumber("ES9121000000000000000002");
        }

        @Test
        @WithMockUser
        @DisplayName("GET /api/accounts/number/{accountNumber} → 404 cuando la cuenta no se encuentra")
        void findByAccountNumber_shouldReturn404WhenNotFound() throws Exception {
            when(accountService.findByAccountNumber("00000000X"))
                    .thenThrow(new AccountNotFoundException("Cuenta no encontrada"));

            mockMvc.perform(get("/api/accounts/number/00000000X"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class findByCustomerIdTest{

        @Test
        @WithMockUser
        @DisplayName("GET /api/accounts/customer/{customerId} → 200 cuando la cuenta se encuentra")
        void findByCustomerId_shouldReturn200WhenFound() throws Exception {
            when(accountService.findByCustomerId(1L)).thenReturn(List.of(accountDTO));

            mockMvc.perform(get("/api/accounts/customer/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].customer_id").value(1L));

            verify(accountService).findByCustomerId(1L);
        }

        @Test
        @WithMockUser
        @DisplayName("GET /api/accounts/customer/{customerId} → 404 cuando la cuenta no se encuentra")
        void findByCustomerId_shouldReturn404WhenNotFound() throws Exception {
            when(accountService.findByCustomerId(100L))
                    .thenThrow(new AccountNotFoundException("Cuenta no encontrada"));

            mockMvc.perform(get("/api/accounts/customer/100"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class findByCustomerIdWithTransactions{

        @Test
        @WithMockUser
        @DisplayName("GET /customer/{customerId}/transactions → 200 cuando las cuentas se encuentran")
        void findByCustomerIdWithTransactions_shouldReturn200WhenFound() throws Exception {
            when(accountService.findByCustomerIdWithTransactions(1L)).thenReturn(List.of(accountDTO));

            mockMvc.perform(get("/api/accounts/customer/1/transactions"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].customer_id").value(1L));

            verify(accountService).findByCustomerIdWithTransactions(1L);
        }

        @Test
        @WithMockUser
        @DisplayName("GET /api/accounts/customer/{customerId}/transactions → 404 cuando la cuenta no se encuentran")
        void findByCustomerIdWithTransactions_shouldReturn404WhenNotFound() throws Exception {
            when(accountService.findByCustomerIdWithTransactions(100L))
                    .thenThrow(new AccountNotFoundException("Cuenta no encontrada"));

            mockMvc.perform(get("/api/accounts/customer/100/transactions"))
                    .andExpect(status().isNotFound());
        }
    }
}
