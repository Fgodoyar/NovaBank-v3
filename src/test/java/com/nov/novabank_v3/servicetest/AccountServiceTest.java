package com.nov.novabank_v3.servicetest;

import com.nov.novabank_v3.dto.AccountDTO;
import com.nov.novabank_v3.exception.AccountNotFoundException;
import com.nov.novabank_v3.exception.CustomerNotFoundException;
import com.nov.novabank_v3.mapper.AccountMapper;
import com.nov.novabank_v3.model.Account;
import com.nov.novabank_v3.model.Customer;
import com.nov.novabank_v3.repository.AccountRepository;
import com.nov.novabank_v3.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Long customerId;
    private String accountNumber;
    private Customer customer;
    private Account account;
    private AccountDTO accountDTO;

    @BeforeEach
    void setUp() {
        customerId = 1L;
        accountNumber = "ES9121000000000000000002";

        customer = Customer.builder()
                .customerId(customerId)
                .customer_name("Pepillo")
                .last_name("Grillo")
                .dni("76543210A")
                .email("pepeergrillo@email.com")
                .phoneNumber("654789345")
                .creationDate(LocalDateTime.now())
                .build();

        account = Account.builder()
                .accountId(1L)
                .accountNumber(accountNumber)
                .accountHolder("Pepillo")
                .balance(BigDecimal.ZERO)
                .customer(customer)
                .build();

        accountDTO = AccountDTO.builder()
                .account_id(1L)
                .account_number(accountNumber)
                .account_holder("Pepillo")
                .balance(BigDecimal.ZERO)
                .customer_id(customerId)
                .build();
    }

    @Nested
    class CreateAccountTest {

        @Test
        void createAccount_customerNotFound_shouldThrowException() {
            when(customerRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(CustomerNotFoundException.class, () ->
                    accountService.createAccount(99L)
            );
            verify(accountRepository, never()).save(any());
        }

        @Test
        void createAccount_customerAlreadyHasAccount_shouldThrowException() {
            when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
            when(accountRepository.existsByCustomerCustomerId(customerId)).thenReturn(true);

            assertThrows(IllegalArgumentException.class, () ->
                    accountService.createAccount(customerId)
            );
            verify(accountRepository, never()).save(any());
        }

        @Test
        void createAccount_validData_shouldSaveSuccessfully() {
            when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
            when(accountRepository.existsByCustomerCustomerId(customerId)).thenReturn(false);
            when(accountMapper.toEntity(any())).thenReturn(account);
            when(accountRepository.save(any())).thenReturn(account);
            when(accountMapper.toDTO(account)).thenReturn(accountDTO);

            AccountDTO result = accountService.createAccount(customerId);

            assertEquals(accountDTO, result);
            verify(accountRepository).save(any());
        }
    }

    @Nested
    class FindByCustomerIdTest {

        @Test
        void findByCustomerId_shouldReturnList() {
            when(accountRepository.findByCustomerCustomerId(customerId)).thenReturn(List.of(account));
            when(accountMapper.toDTO(account)).thenReturn(accountDTO);

            List<AccountDTO> result = accountService.findByCustomerId(customerId);

            assertEquals(1, result.size());
            verify(accountRepository).findByCustomerCustomerId(customerId);
        }

        @Test
        void findByCustomerId_shouldReturnEmptyList() {
            when(accountRepository.findByCustomerCustomerId(customerId)).thenReturn(List.of());

            List<AccountDTO> result = accountService.findByCustomerId(customerId);

            assertEquals(0, result.size());
            verify(accountRepository).findByCustomerCustomerId(customerId);
        }
    }

    @Nested
    class FindByAccountNumberTest {

        @Test
        void findByAccountNumber_existingAccount_shouldReturnAccount() {
            when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));
            when(accountMapper.toDTO(account)).thenReturn(accountDTO);

            AccountDTO result = accountService.findByAccountNumber(accountNumber);

            assertEquals(accountDTO, result);
            verify(accountRepository).findByAccountNumber(accountNumber);
        }

        @Test
        void findByAccountNumber_nonExistingAccount_shouldThrowException() {
            when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());

            assertThrows(AccountNotFoundException.class, () ->
                    accountService.findByAccountNumber(accountNumber)
            );
            verify(accountRepository).findByAccountNumber(accountNumber);
        }
    }

    @Nested
    class FindByCustomerIdWithTransactionsTest {

        @Test
        void findByCustomerIdWithTransactions_shouldReturnList() {
            when(accountRepository.findByCustomerIdWithTransactions(customerId)).thenReturn(List.of(account));
            when(accountMapper.toDTO(account)).thenReturn(accountDTO);

            List<AccountDTO> result = accountService.findByCustomerIdWithTransactions(customerId);

            assertEquals(1, result.size());
            verify(accountRepository).findByCustomerIdWithTransactions(customerId);
        }

        @Test
        void findByCustomerIdWithTransactions_shouldReturnEmptyList() {
            when(accountRepository.findByCustomerIdWithTransactions(customerId)).thenReturn(List.of());

            List<AccountDTO> result = accountService.findByCustomerIdWithTransactions(customerId);

            assertEquals(0, result.size());
            verify(accountRepository).findByCustomerIdWithTransactions(customerId);
        }
    }
}