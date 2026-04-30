package com.nov.novabank_v3.service;

import com.nov.novabank_v3.dto.TransactionDTO;

import java.math.BigDecimal;
import java.util.List;

public interface OperationService {
    TransactionDTO deposit(String accountNumber, BigDecimal balance);
    TransactionDTO withdraw(String accountNumber, BigDecimal amount);
    List<TransactionDTO> transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount);
}
