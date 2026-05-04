package com.nov.novabank_v3.servicetest;

import com.nov.novabank_v3.dto.TransactionDTO;
import com.nov.novabank_v3.exception.InsufficientBalanceException;
import com.nov.novabank_v3.mapper.TransactionMapper;
import com.nov.novabank_v3.model.Account;
import com.nov.novabank_v3.repository.AccountRepository;
import com.nov.novabank_v3.repository.TransactionRepository;
import com.nov.novabank_v3.service.OperationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OperationServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private OperationServiceImpl operationService;

    private Account sourceAccount;
    private Account destinationAccount;
    private BigDecimal amount;

    @BeforeEach
    void setUp() {
        amount = new BigDecimal("500.00");

        sourceAccount = Account.builder()
                .accountId(1L)
                .accountNumber("ES9121000000000000000002")
                .accountHolder("Pepillo")
                .balance(new BigDecimal("1000.00"))
                .build();

        destinationAccount = Account.builder()
                .accountId(2L)
                .accountNumber("ES9121000000000000000003")
                .accountHolder("Pepilla")
                .balance(new BigDecimal("500.00"))
                .build();
    }

    @Nested
    class DepositTest {

        @Test
        void deposit_validData_shouldReturnTransactionDTO() {
            when(accountRepository.findByAccountNumber("ES9121000000000000000002"))
                    .thenReturn(Optional.of(sourceAccount));

            TransactionDTO result = operationService.deposit("ES9121000000000000000002", amount);

            assertNotNull(result);
            assertEquals(new BigDecimal("1500.00"), sourceAccount.getBalance());
            verify(accountRepository).save(sourceAccount);
            verify(transactionRepository).save(any());
        }

        @Test
        void deposit_accountNotFound_shouldThrowException() {
            when(accountRepository.findByAccountNumber("ES0000000000000000000000"))
                    .thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () ->
                    operationService.deposit("ES0000000000000000000000", amount)
            );
            verify(accountRepository, never()).save(any());
        }

        @Test
        void deposit_negativeAmount_shouldThrowException() {
            when(accountRepository.findByAccountNumber("ES9121000000000000000002"))
                    .thenReturn(Optional.of(sourceAccount));

            assertThrows(IllegalArgumentException.class, () ->
                    operationService.deposit("ES9121000000000000000002", new BigDecimal("-100.00"))
            );
            verify(accountRepository, never()).save(any());
        }
    }

    @Nested
    class WithdrawTest {

        @Test
        void withdraw_validData_shouldReturnTransactionDTO() {
            when(accountRepository.findByAccountNumber("ES9121000000000000000002"))
                    .thenReturn(Optional.of(sourceAccount));

            TransactionDTO result = operationService.withdraw("ES9121000000000000000002", amount);

            assertNotNull(result);
            assertEquals(new BigDecimal("500.00"), sourceAccount.getBalance());
            verify(accountRepository).save(sourceAccount);
            verify(transactionRepository).save(any());
        }

        @Test
        void withdraw_accountNotFound_shouldThrowException() {
            when(accountRepository.findByAccountNumber("ES0000000000000000000000"))
                    .thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () ->
                    operationService.withdraw("ES0000000000000000000000", amount)
            );
            verify(accountRepository, never()).save(any());
        }

        @Test
        void withdraw_negativeAmount_shouldThrowException() {
            when(accountRepository.findByAccountNumber("ES9121000000000000000002"))
                    .thenReturn(Optional.of(sourceAccount));

            assertThrows(IllegalArgumentException.class, () ->
                    operationService.withdraw("ES9121000000000000000002", new BigDecimal("-100.00"))
            );
            verify(accountRepository, never()).save(any());
        }

        @Test
        void withdraw_insufficientBalance_shouldThrowException() {
            when(accountRepository.findByAccountNumber("ES9121000000000000000002"))
                    .thenReturn(Optional.of(sourceAccount));

            assertThrows(InsufficientBalanceException.class, () ->
                    operationService.withdraw("ES9121000000000000000002", new BigDecimal("9999.00"))
            );
            verify(accountRepository, never()).save(any());
        }
    }

    @Nested
    class TransferTest {

        @Test
        void transfer_validData_shouldReturnTwoTransactions() {
            when(accountRepository.findByAccountNumber("ES9121000000000000000002"))
                    .thenReturn(Optional.of(sourceAccount));
            when(accountRepository.findByAccountNumber("ES9121000000000000000003"))
                    .thenReturn(Optional.of(destinationAccount));

            List<TransactionDTO> result = operationService.transfer(
                    "ES9121000000000000000002", "ES9121000000000000000003", amount);

            assertEquals(2, result.size());
            assertEquals(new BigDecimal("500.00"), sourceAccount.getBalance());
            assertEquals(new BigDecimal("1000.00"), destinationAccount.getBalance());
            verify(accountRepository, times(2)).save(any());
            verify(transactionRepository, times(2)).save(any());
        }

        @Test
        void transfer_accountNotFound_shouldThrowException() {
            when(accountRepository.findByAccountNumber("ES0000000000000000000000"))
                    .thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () ->
                    operationService.transfer(
                            "ES0000000000000000000000", "ES9121000000000000000003", amount)
            );
            verify(accountRepository, never()).save(any());
        }

        @Test
        void transfer_sameAccount_shouldThrowException() {
            when(accountRepository.findByAccountNumber("ES9121000000000000000002"))
                    .thenReturn(Optional.of(sourceAccount));

            assertThrows(IllegalArgumentException.class, () ->
                    operationService.transfer(
                            "ES9121000000000000000002", "ES9121000000000000000002", amount)
            );
            verify(accountRepository, never()).save(any());
        }

        @Test
        void transfer_insufficientBalance_shouldThrowException() {
            when(accountRepository.findByAccountNumber("ES9121000000000000000002"))
                    .thenReturn(Optional.of(sourceAccount));
            when(accountRepository.findByAccountNumber("ES9121000000000000000003"))
                    .thenReturn(Optional.of(destinationAccount));

            assertThrows(InsufficientBalanceException.class, () ->
                    operationService.transfer(
                            "ES9121000000000000000002", "ES9121000000000000000003", new BigDecimal("9999.00"))
            );
            verify(accountRepository, never()).save(any());
        }

        @Test
        void transfer_negativeAmount_shouldThrowException() {
            when(accountRepository.findByAccountNumber("ES9121000000000000000002"))
                    .thenReturn(Optional.of(sourceAccount));
            when(accountRepository.findByAccountNumber("ES9121000000000000000003"))
                    .thenReturn(Optional.of(destinationAccount));

            assertThrows(IllegalArgumentException.class, () ->
                    operationService.transfer(
                            "ES9121000000000000000002", "ES9121000000000000000003", new BigDecimal("-100.00"))
            );
            verify(accountRepository, never()).save(any());
        }
    }
}