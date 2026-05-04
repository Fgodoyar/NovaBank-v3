package com.nov.novabank_v3.service;

import com.nov.novabank_v3.dto.TransactionDTO;
import com.nov.novabank_v3.mapper.TransactionMapper;
import com.nov.novabank_v3.model.Account;
import com.nov.novabank_v3.repository.AccountRepository;
import com.nov.novabank_v3.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Transactional(readOnly = true)
    @Override
    public List<TransactionDTO> findByAccountId(Long accountId) {
        Account account = accountRepository.findByAccountId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la cuenta con el ID: " + accountId));
        return transactionRepository.findByAccount_AccountId(account.getAccountId()).stream()
                .map(transaction -> TransactionDTO.builder()
                        .transactionId(transaction.getTransactionId())
                        .transactionType(transaction.getTransactionType())
                        .amount(transaction.getAmount())
                        .description(transaction.getDescription())
                        .creationDate(transaction.getCreationDate())
                        .accountId(transaction.getAccount() != null ? transaction.getAccount().getAccountId() : null)
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<TransactionDTO> findByAccountIdAndCreationDateBetweenOrderByCreationDateDesc(Long accountId, LocalDateTime startDate, LocalDateTime endDate) {
        Account account = accountRepository.findByAccountId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la cuenta con el ID: " + accountId));

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("La fecha de inicio debe ser anterior a la fecha de fin.");
        }

        return transactionRepository.findByAccount_AccountIdAndCreationDateBetweenOrderByCreationDateDesc(account.getAccountId(), startDate, endDate).stream()
                .map(transaction -> TransactionDTO.builder()
                        .transactionId(transaction.getTransactionId())
                        .transactionType(transaction.getTransactionType())
                        .amount(transaction.getAmount())
                        .description(transaction.getDescription())
                        .creationDate(transaction.getCreationDate())
                        .accountId(transaction.getAccount() != null ? transaction.getAccount().getAccountId() : null)
                        .build())
                .collect(Collectors.toList());
    }
}
