package com.nov.novabank_v3.servicetest;

import com.nov.novabank_v3.dto.TransactionDTO;
import com.nov.novabank_v3.mapper.TransactionMapper;
import com.nov.novabank_v3.model.Account;
import com.nov.novabank_v3.model.Transaction;
import com.nov.novabank_v3.repository.AccountRepository;
import com.nov.novabank_v3.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Long accountId;
    private Account account;
    private Transaction transaction;
    private TransactionDTO transactionDTO;

    @BeforeEach
    void setUp() {
        accountId = 1L;

        account = Account.builder()
                .accountId(accountId)
                .accountNumber("ES9121000000000000000002")
                .accountHolder("Pepillo")
                .balance(new BigDecimal("1500.00"))
                .build();

        transaction = Transaction.builder()
                .transactionId(1L)
                .transactionType("transaccion_entrante")
                .amount(new BigDecimal("500.00"))
                .description("Transferencia recibida")
                .account(account)
                .build();

        transactionDTO = TransactionDTO.builder()
                .transaction_id(1L)
                .transaction_type("transaccion_entrante")
                .amount(new BigDecimal("500.00"))
                .description("Transferencia recibida")
                .account_id(accountId)
                .build();
    }

    @Nested
    class FindByAccountIdTest {

        @Test
        void findByAccountId_shouldReturnList() {
            when(accountRepository.findByAccountId(accountId)).thenReturn(Optional.of(account));
            when(transactionRepository.findByAccount_AccountId(accountId)).thenReturn(List.of(transaction));
            when(transactionMapper.toDTO(transaction)).thenReturn(transactionDTO);

            List<TransactionDTO> result = transactionService.findByAccountId(accountId);

            assertEquals(1, result.size());
            verify(accountRepository).findByAccountId(accountId);
            verify(transactionRepository).findByAccount_AccountId(accountId);
        }

        @Test
        void findByAccountId_shouldReturnEmptyList() {
            when(accountRepository.findByAccountId(accountId)).thenReturn(Optional.of(account));
            when(transactionRepository.findByAccount_AccountId(accountId)).thenReturn(List.of());

            List<TransactionDTO> result = transactionService.findByAccountId(accountId);

            assertEquals(0, result.size());
        }

        @Test
        void findByAccountId_accountNotFound_shouldThrowException() {
            when(accountRepository.findByAccountId(99L)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () ->
                    transactionService.findByAccountId(99L)
            );
            verify(transactionRepository, never()).findByAccount_AccountId(any());
        }
    }

    @Nested
    class FindByAccountIdAndDateRangeTest {

        @Test
        void findByDateRange_shouldReturnList() {

            LocalDateTime start = LocalDate.of(2026, 1, 1).atStartOfDay();
            LocalDateTime end = LocalDate.of(2026, 12, 31).atStartOfDay();

            when(accountRepository.findByAccountId(accountId)).thenReturn(Optional.of(account));
            when(transactionRepository.findByAccount_AccountIdAndCreationDateBetweenOrderByCreationDateDesc(
                    accountId, start, end)).thenReturn(List.of(transaction));
            when(transactionMapper.toDTO(transaction)).thenReturn(transactionDTO);

            List<TransactionDTO> result = transactionService
                    .findByAccountIdAndCreationDateBetweenOrderByCreationDateDesc(accountId, start, end);

            assertEquals(1, result.size());
            verify(accountRepository).findByAccountId(accountId);
            verify(transactionRepository).findByAccount_AccountIdAndCreationDateBetweenOrderByCreationDateDesc(
                    accountId, start, end);
        }

        @Test
        void findByDateRange_shouldReturnEmptyList() {

            LocalDateTime start = LocalDate.of(2026, 1, 1).atStartOfDay();
            LocalDateTime end = LocalDate.of(2026, 12, 31).atStartOfDay();

            when(accountRepository.findByAccountId(accountId)).thenReturn(Optional.of(account));
            when(transactionRepository.findByAccount_AccountIdAndCreationDateBetweenOrderByCreationDateDesc(
                    accountId, start, end)).thenReturn(List.of());

            List<TransactionDTO> result = transactionService
                    .findByAccountIdAndCreationDateBetweenOrderByCreationDateDesc(accountId, start, end);

            assertEquals(0, result.size());
        }

        @Test
        void findByDateRange_accountNotFound_shouldThrowException() {

            LocalDateTime start = LocalDate.of(2026, 1, 1).atStartOfDay();
            LocalDateTime end = LocalDate.of(2026, 12, 31).atStartOfDay();

            when(accountRepository.findByAccountId(99L)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () ->
                    transactionService.findByAccountIdAndCreationDateBetweenOrderByCreationDateDesc(
                            99L, start, end)
            );

            verify(transactionRepository, never())
                    .findByAccount_AccountIdAndCreationDateBetweenOrderByCreationDateDesc(any(), any(), any());
        }
    }
}