package com.nov.novabank_v3.service;

import com.nov.novabank_v3.dto.TransactionDTO;
import com.nov.novabank_v3.exception.InsufficientBalanceException;
import com.nov.novabank_v3.factory.TransactionFactory;
import com.nov.novabank_v3.mapper.TransactionMapper;
import com.nov.novabank_v3.model.Account;
import com.nov.novabank_v3.model.Transaction;
import com.nov.novabank_v3.repository.AccountRepository;
import com.nov.novabank_v3.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionMapper transactionMapper;

    @Transactional(readOnly = true)
    @Override
    public List<TransactionDTO> findByAccountId(Long accountId) {
        Account account = accountRepository.findByAccountId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la cuenta con el ID: " + accountId));
        return transactionRepository.findByAccountId(account.getAccount_id()).stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<TransactionDTO> findByAccountIdAndCreationDateBetweenOrderByCreationDateDesc(Long accountId, LocalDate startDate, LocalDate endDate){
        Account account = accountRepository.findByAccountId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la cuenta con el ID: " + accountId));
        return transactionRepository.findByAccountIdAndCreationDateBetweenOrderByCreationDateDesc(account.getAccount_id(), startDate, endDate).stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }
}
