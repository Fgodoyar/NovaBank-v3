package com.nov.novabank_v3.service;

import com.nov.novabank_v3.dto.TransactionDTO;
import com.nov.novabank_v3.model.Transaction;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public interface TransactionService {
    List<TransactionDTO> findByAccountId(Long accountId);
    List<TransactionDTO> findByAccountIdAndCreationDateBetweenOrderByCreationDateDesc(Long accountId, LocalDate startDate, LocalDate endDate);
    TransactionDTO deposit(String accountNumber, BigDecimal balance);
    TransactionDTO withdraw(String accountNumber, BigDecimal amount);
    List<TransactionDTO> transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount);
}
